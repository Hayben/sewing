package com.sidooo.sewing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchStatus;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.point.Point;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.seed.Statistics;
import com.sidooo.senode.DatawareConfiguration;
import com.sidooo.sewing.Analyst.PointMapper;

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
				output.collect(key, SUCCESS);
				if (status.hasExpired()) {
					output.collect(key, UPDATE);
				}
			} else {
				if (status.hasSizeLimit()) {
					output.collect(key, LIMIT);
				} else {
					if (status.hasRetryLimit()) {
						output.collect(key, FAIL);
					} else {
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
			
			output.collect(key, value);
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
				
				if (value == SUCCESS) {
					stat.success ++;
				} else if (value == FAIL) {
					stat.fail ++;
				} else if (value == WAIT) {
					stat.wait ++;
				} else if (value == UPDATE) {
					stat.update ++;
				} else if (value == LIMIT) {
					stat.limit ++;
				} else {
					
				}
			}
			
			seedService.updateStatistics(seedId, stat);
			
		}
		
	}

	@Override
	public int run(String[] args) throws Exception {
		
		JobConf job = new JobConf(getConf(), Counter.class);

		job.setJobName("Sewing Counter");

		List<Seed> seeds = seedService.getSeeds();
		submitSeedCache(job, seeds);
		submitNlpCache(job);

		submitItemInput(job);

		submitPointOutput(job);

		job.setMapperClass(PointMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Point.class);
		job.setNumReduceTasks(0);

		JobClient.runJob(job);
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		Counter counter = context.getBean("counter", Counter.class);

		int res = ToolRunner.run(SewingConfiguration.create(), counter, args);
		System.exit(res);
	}

}
