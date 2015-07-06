package com.sidooo.sewing;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchStatus;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.seed.Statistics;
import com.sidooo.senode.MongoConfiguration;

@Service("counter")
public class Counter extends SewingConfigured implements Tool{
	
	@Autowired
	private SeedService seedService;

	public static final LongWritable SUCCESS = new LongWritable(0);
	public static final LongWritable FAIL = new LongWritable(1);
	public static final LongWritable WAIT = new LongWritable(2);
	public static final LongWritable UPDATE = new LongWritable(3);
	public static final LongWritable LIMIT	= new LongWritable(4);
	
	public static class ReadFetchStatusMapper extends 
		Mapper<Text, FetchContent, Text, FetchStatus> {

		@Override
		public void map(Text key, FetchContent value, Context context)
				throws IOException, InterruptedException {
			
			FetchStatus status = new FetchStatus();
			status.setStatus(value.getStatus());
			status.setFetchTime(value.getTimeStamp());
			context.write(key, status);
		}

	}
	
	public static class CalcUrlResultReducer extends 
		Reducer<Text, FetchStatus, Text, LongWritable> {

		@Override
		public void reduce(Text key, Iterable<FetchStatus> values, Context context)
				throws IOException, InterruptedException {
			
			UrlStatus status = UrlStatus.from(values);
			if (status == UrlStatus.READY) {
				context.write(key, WAIT);
			} else if (status == UrlStatus.LATEST){
				context.write(key, SUCCESS);
			} else if (status == UrlStatus.FILTERED) {
				context.write(key, LIMIT);
			} else if (status == UrlStatus.UNREACHABLE) {
				context.write(key, FAIL);
			} else {
				LOG.info("Unknown Url Status.");
			}
		}
		
	}
	
	//将URL按照种子设置分类
	public static class ClassifyUrlMapper extends 
		Mapper<Text, LongWritable, Text, LongWritable> {
		
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
		public void map(Text key, LongWritable value, Context context)
				throws IOException, InterruptedException {
			
			String url = key.toString();
			Seed seed = SeedService.getSeedByUrl(url, seeds);
			if (seed == null) {
				return;
			}
			
			LOG.info("URL: " + url + ", Seed: " + seed.getId());
			context.write(new Text(seed.getId()), value);
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
			Statistics stat = new Statistics();
			
			for(LongWritable value : values) {
				
				if (value.get() == SUCCESS.get()) {
					stat.success ++;
				} else if (value.get() == FAIL.get()) {
					stat.fail ++;
				} else if (value.get() == WAIT.get()) {
					stat.wait ++;
				} else if (value.get() == LIMIT.get()) {
					stat.limit ++;
				} else {
					
				}
			}
			
			LOG.info(seedId+" Statistics: " + stat.toString());
			seedService.updateStatistics(seedId, stat);
			
		}
		
	}
	
	private void listUrl() throws Exception {
		Job job = new Job(getConf());
		job.setJobName("Sewing Counter 1");
		job.setJarByClass(Counter.class);

		TaskData.submitCrawlInput(job);

		TaskData.submitCountOutput(job);

		job.setMapperClass(ReadFetchStatusMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FetchStatus.class);
		job.setReducerClass(CalcUrlResultReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(5);

		job.waitForCompletion(true);
	}
	
	private void countUrl() throws Exception {
		Job job = new Job(getConf());
		job.setJobName("Sewing Counter 2");
		job.setJarByClass(Counter.class);
		
		List<Seed> seeds = seedService.getSeeds();
		CacheSaver.submitSeedCache(job, seeds);
		TaskData.submitCountInput(job);

		TaskData.submitNullOutput(job);

		job.setMapperClass(ClassifyUrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(2);

		job.waitForCompletion(true);
		
	}

	@Override
	public int run(String[] args) throws Exception {
		
		listUrl();
		
		countUrl();

		return 0;
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
