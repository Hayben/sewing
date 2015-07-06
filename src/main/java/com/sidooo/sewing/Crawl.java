package com.sidooo.sewing;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.map.MultithreadedMapper;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.HttpFetcher;
import com.sidooo.senode.MongoConfiguration;

@Service("crawl")
public class Crawl extends Configured implements Tool {
	
	private static final int VCORE_COUNT = 4;
	
	public static final Logger LOG = LoggerFactory.getLogger("Crawl");

	public static class CrawlMapper extends
			Mapper<Text, Text, Text, FetchContent> {

		HttpFetcher fetcher;
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			fetcher = new HttpFetcher();
		}

		@Override
		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {

			FetchContent content = fetcher.fetch(key.toString());
			
			LOG.info("Fetch " + key.toString()  
					+ ", Response:" + content.getStatus()
					+ ", Size:" + content.getContentSize());
			if (content.getStatus() == 190) {
				context.getCounter("Sewing", "INVALID").increment(1);
			} else if (content.getStatus() == 200) {
				context.getCounter("Sewing", "SUCCESS").increment(1);
			} else if (content.getStatus() == 199) {
				context.getCounter("Sewing", "SIZELIMIT").increment(1);
			} else {
				context.getCounter("Sewing", "FAIL").increment(1);
			}
			context.write(key, content);
		}
	}

	public static class CrawlReducer extends
			Reducer<Text, FetchContent, Text, FetchContent> {

		@Override
		public void reduce(Text key, Iterable<FetchContent> values,
				Context context) throws IOException, InterruptedException {
			Iterator<FetchContent> contents = values.iterator();
			if (contents.hasNext()) {
				FetchContent content = contents.next();
				context.write(key, content);
				if (contents.hasNext()) {
					context.getCounter("Sewing", "DUPLICATE").increment(1);
				}
			}
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// 创建分布式爬虫任务
		Configuration conf = getConf();
		conf.setInt("mapreduce.map.cpu.vcores", VCORE_COUNT);
		LOG.info("mapreduce.map.cpu.vcores : " + getConf().getInt("mapreduce.map.cpu.vcores", 1));
		
		Job job = new Job(getConf());
		job.setJobName("Sewing Crawl");
		job.setJarByClass(Crawl.class);

		// 设置输入
		TaskData.submitUrlInput(job);

		// 设置输出
		TaskData.submitCrawlOutput(job);

		// 设置分布式计算流程
		job.setMapperClass(MultithreadedMapper.class);
		MultithreadedMapper.setMapperClass(job, CrawlMapper.class);
		MultithreadedMapper.setNumberOfThreads(job, VCORE_COUNT * 3);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FetchContent.class);
		
		job.setReducerClass(CrawlReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FetchContent.class);
		job.setNumReduceTasks(1);

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			long successCount = group.findCounter("SUCCESS").getValue();
			System.out.println("Success Count: " + successCount);
			long failCount = group.findCounter("FAIL").getValue();
			System.out.println("Fail Count: " + failCount);
			long limitCount = group.findCounter("SIZELIMIT").getValue();
			System.out.println("SizeLimit Count: " + limitCount);
			long dupCount = group.findCounter("DUPLICATE").getValue();
			System.out.println("Duplicate Count: " + dupCount);
			long invalidCount = group.findCounter("INVALID").getValue();
			System.out.println("Invalid Count: " + invalidCount);
			return 0;
		} else {
			return 1;
		}
	}

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		Crawl crawl = context.getBean("crawl", Crawl.class);

		int res = ToolRunner.run(SewingConfiguration.create(), crawl, args);
		System.exit(res);
	}

}
