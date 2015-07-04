package com.sidooo.sewing;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
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
import com.sidooo.senode.DatawareConfiguration;
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
	
	public static class ReadFetchStatusMapper extends SewingMapReduce implements
		Mapper<Text, FetchContent, Text, FetchStatus> {

		@Override
		public void map(Text key, FetchContent value,
				OutputCollector<Text, FetchStatus> output, Reporter reporter)
				throws IOException {
			
			FetchStatus status = new FetchStatus();
			status.setStatus(value.getStatus());
			status.setFetchTime(value.getTimeStamp());
			output.collect(key, status);
		}
		
		
	}
	
	public static class CalcUrlResultReducer extends SewingMapReduce implements
		Reducer<Text, FetchStatus, Text, LongWritable> {

		@Override
		public void reduce(Text key, Iterator<FetchStatus> values,
				OutputCollector<Text, LongWritable> output, Reporter reporter)
				throws IOException {
			
			UrlStatus status = new UrlStatus(values);
			
			if (status.hasSucceed()) {
				LOG.info("URL:" + key.toString() + ", SUCCESS");
				output.collect(key, SUCCESS);
				if (status.hasExpired()) {
					LOG.info("URL:" + key.toString() + ", UPDATE");
					output.collect(key, UPDATE);
				}
			} else {
				if (status.hasSizeLimit()) {
					LOG.info("URL:" + key.toString() + ", LIMIT");
					output.collect(key, LIMIT);
				} else {
					if (status.hasRetryLimit()) {
						LOG.info("URL:" + key.toString() + ", FAIL");
						output.collect(key, FAIL);
					} else {
						LOG.info("URL:" + key.toString() + ", WAIT");
						output.collect(key, WAIT);
					}
				}
			}
		}
		
	}
	
	//将URL按照种子设置分类
	public static class ClassifyUrlMapper extends SewingMapReduce implements
		Mapper<Text, LongWritable, Text, LongWritable> {
		
		@Override
		public void configure(JobConf conf) {
			checkCacheFiles(conf);
		}

		@Override
		public void map(Text key, LongWritable value,
				OutputCollector<Text, LongWritable> output, Reporter reporter)
				throws IOException {
			
			String url = key.toString();
			Seed seed = getSeedByUrl(url);
			if (seed == null) {
				return;
			}
			
			LOG.info("URL: " + url + ", Seed: " + seed.getId());
			output.collect(new Text(seed.getId()), value);
		}
		
		
	}
	
	public static class CountReducer extends SewingMapReduce implements
		Reducer<Text, LongWritable, NullWritable, NullWritable> {

		private SeedService seedService; 
		
		@Override
		public void configure(JobConf conf) {
			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
					DatawareConfiguration.class);
			context.scan("com.sidooo");
			seedService = context.getBean("seedService", SeedService.class);
		}
		
		@Override
		public void reduce(Text key, Iterator<LongWritable> values,
				OutputCollector<NullWritable, NullWritable> output,
				Reporter reporter) throws IOException {
			
			String seedId = key.toString();
			Statistics stat = new Statistics();
			
			while(values.hasNext()) {
				
				LongWritable value = values.next();
				
				if (value.get() == SUCCESS.get()) {
					stat.success ++;
				} else if (value.get() == FAIL.get()) {
					stat.fail ++;
				} else if (value.get() == WAIT.get()) {
					stat.wait ++;
				} else if (value.get() == UPDATE.get()) {
					stat.update ++;
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
		JobConf job = new JobConf(getConf(), Counter.class);

		job.setJobName("Sewing Counter 1");
		
		submitCrawlInput(job);

		submitCountOutput(job);

		job.setMapperClass(ReadFetchStatusMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FetchStatus.class);
		job.setReducerClass(CalcUrlResultReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(5);

		JobClient.runJob(job);
	}
	
	private void countUrl() throws Exception {
		JobConf job = new JobConf(getConf(), Counter.class);

		job.setJobName("Sewing Counter 2");
		
		List<Seed> seeds = seedService.getSeeds();
		submitSeedCache(job, seeds);
		submitCountInput(job);

		submitNullOutput(job);

		job.setMapperClass(ClassifyUrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(2);

		JobClient.runJob(job);
		
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
