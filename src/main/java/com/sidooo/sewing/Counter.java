package com.sidooo.sewing;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.UrlStatus;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("counter")
public class Counter extends Configured implements Tool{
	
	public static final Logger LOG = LoggerFactory.getLogger("Counter");
	
	@Autowired
	private SeedService seedService;

	public static final LongWritable SUCCESS = new LongWritable(0);
	public static final LongWritable FAIL = new LongWritable(1);
	public static final LongWritable WAIT = new LongWritable(2);
	public static final LongWritable UPDATE = new LongWritable(3);
	public static final LongWritable LIMIT	= new LongWritable(4);
	
	
	//将URL按照种子设置分类
	public static class UrlMapper extends 
		Mapper<Text, IntWritable, Text, LongWritable> {
		
		private List<Seed> seeds;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			Configuration conf = context.getConfiguration();
			seeds = CacheLoader.loadSeedList(conf);
			if (seeds == null) {
				throw new InterruptedException("Seed List is null.");
			}
		}

		@Override
		public void map(Text key, IntWritable value, Context context)
				throws IOException, InterruptedException {
			
			String url = key.toString();
			Seed seed = SeedService.getSeedByUrl(url, seeds);
			if (seed == null) {
				return;
			}
			
			UrlStatus status = UrlStatus.valueOf(value.get());
			
			if (status == UrlStatus.READY) {
				context.write(new Text(seed.getId()), WAIT);
			} else if (status == UrlStatus.LATEST){
				context.write(new Text(seed.getId()), SUCCESS);
			} else if (status == UrlStatus.FILTERED) {
				context.write(new Text(seed.getId()), LIMIT);
			} else if (status == UrlStatus.UNREACHABLE) {
				context.write(new Text(seed.getId()), FAIL);
			} else {
				LOG.info("Unknown Url Status.");
			}

			//LOG.info("URL: " + url + ", Seed: " + seed.getId());
			
		}
		
		
	}
	
	public static class CountReducer extends 
		Reducer<Text, LongWritable, NullWritable, NullWritable> {

		private SeedService seedService; 
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class);
			appcontext.scan("com.sidooo.seed");
			seedService = appcontext.getBean("seedService", SeedService.class);
		}
		
		@Override
		public void reduce(Text key, Iterable<LongWritable> values, Context context) 
				throws IOException, InterruptedException {
			
			String seedId = key.toString();
			
			long successCount = 0;
			long failCount = 0;
			long waitCount = 0;
			long limitCount = 0;
			for(LongWritable value : values) {
				
				if (value.get() == SUCCESS.get()) {
					successCount ++;
				} else if (value.get() == FAIL.get()) {
					failCount ++;
				} else if (value.get() == WAIT.get()) {
					waitCount ++;
				} else if (value.get() == LIMIT.get()) {
					limitCount ++;
				} else {
					
				}
			}
			
			seedService.updateFetchStatistics(seedId, successCount, failCount, waitCount, limitCount);
			
		}
		
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Job job = new Job(getConf());
		job.setJobName("Sewing Counter");
		job.setJarByClass(Counter.class);

		List<Seed> seeds = seedService.getEnabledSeeds();
		if (seeds.size() <= 0) {
			System.out.println("Seed Count is 0.");
			return 1;
		}
		CacheSaver.submitSeedCache(job, seeds);
		TaskData.submitUrldbInput(job);

		TaskData.submitCountOutput(job);

		job.setMapperClass(UrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setReducerClass(CountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(5);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		Counter counter = context.getBean("counter", Counter.class);

		int res = ToolRunner.run(SewingConfiguration.create(), counter, args);
		System.exit(res);
	}

}
