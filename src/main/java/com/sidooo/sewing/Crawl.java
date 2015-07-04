package com.sidooo.sewing;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
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
public class Crawl extends SewingConfigured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("Crawl");

	public static class CrawlMapper extends
			Mapper<LongWritable, Text, Text, FetchContent> {

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			URL url = null;
			try {
				url = new URL(value.toString());
			} catch (Exception e) {
				LOG.error("Invalid Url: " + value.toString());
				context.getCounter("Sewing", "INVALID").increment(1);
				return;
			}

			HttpFetcher fetcher = new HttpFetcher(url);
			FetchContent content = null;
			try {
				content = fetcher.fetch();
			} catch (Exception e) {
				LOG.error("Fetch Content Failed.", e);
				context.getCounter("Sewing", "FAIL").increment(1);
				return;
			}

			if (content != null) {
				context.write(value, content);
				LOG.info("Fetch Content" + ", Response:" + content.getStatus()
						+ ", Size:" + content.getContentSize());
				context.getCounter("Sewing", "SUCCESS").increment(1);
			} else {
				context.getCounter("Sewing", "NULL").increment(1);
			}
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
					LOG.warn("Duplicate Fetch.");
					context.getCounter("Sewing", "DUPLICATE").increment(1);
				}
			}
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// 创建分布式爬虫任务
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
		MultithreadedMapper.setNumberOfThreads(job, 8);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FetchContent.class);
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
			long dupCount = group.findCounter("DUPLICATE").getValue();
			System.out.println("Duplicate Count: " + dupCount);
			long nullCount = group.findCounter("NULL").getValue();
			System.out.println("Null Count: " + nullCount);
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
