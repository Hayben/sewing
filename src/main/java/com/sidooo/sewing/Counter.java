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
import com.sidooo.extractor.ContentDetector;
import com.sidooo.extractor.ContentType;
import com.sidooo.point.ItemRepository;
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
			
			URL url = new URL(key.toString());
			FetchStatus status = new FetchStatus();
			status.setStatus(value.getStatus());
			status.setFetchTime(value.getTimeStamp());
			output.collect(key, status);
		}
		
		
	}
	
	public static class CalcUrlResultReducer extends SewingMapReduce implements
		Reducer<Text, FetchStatus, Text, LongWritable> {
		
		private long PERIOD = 10 * 24 * 60 * 1000;

		private List<FetchStatus> toList(Iterator<FetchStatus> it)  {
			List<FetchStatus> status = new ArrayList<FetchStatus>();
			while(it.hasNext()) {
				status.add(it.next());
			}
			
			return status;
		}
		
		private boolean hasSizeLimit(List<FetchStatus> status) {
			for(FetchStatus it : status) {
				if (it.getStatus() == 199) {
					return true;
				}
			}
			
			return false;
		}
		
		private boolean hasRetryLimit(List<FetchStatus> status) {
			int count = 0;
			for(FetchStatus it : status) {
				int response = it.getStatus();
				if (response != 200 &&
						response != 0 &&
						response != 1 &&
						response != 199 ) {
					count ++;
				}
			}
			
			return count >= 15;
		}
		
		private boolean hasExpired(List<FetchStatus> status) {
			for(FetchStatus it : status) {
				
				if (it.getStatus() == 200 &&
					(System.currentTimeMillis() - it.getFetchTime()) < PERIOD) {
					return true;
				}
			}
			
			return true;
		}
		
		private boolean hasSucceed(List<FetchStatus> status) {
			
			for (FetchStatus it : status) {
				if (it.getStatus() == 200) {
					return true;
				}
			}
			
			return false;
		}
		

		@Override
		public void reduce(Text key, Iterator<FetchStatus> values,
				OutputCollector<Text, LongWritable> output, Reporter reporter)
				throws IOException {
			

			List<FetchStatus> status = toList(values);
			
			if (hasSucceed(status)) {
				output.collect(key, SUCCESS);
				if (hasExpired(status)) {
					output.collect(key, UPDATE);
				}
			} else {
				if (hasSizeLimit(status)) {
					output.collect(key, LIMIT);
				} else {
					if (hasRetryLimit(status)) {
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
