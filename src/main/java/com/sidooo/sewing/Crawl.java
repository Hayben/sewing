package com.sidooo.sewing;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapRunnable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.HttpFetcher;
import com.sidooo.senode.DatawareConfiguration;
import com.sidooo.senode.MongoConfiguration;

@Service("crawl")
public class Crawl extends SewingConfigured implements Tool,
		MapRunnable<LongWritable, Text, Text, FetchContent> {
	
	@Override
	public void configure(JobConf conf) {

	}

	@Override
	public void run(RecordReader<LongWritable, Text> inputReader,
			OutputCollector<Text, FetchContent> output, Reporter reporter)
			throws IOException {

		boolean hasMore = true;
		while (hasMore) {
			LongWritable lineId = new LongWritable();
			Text line = new Text();
			hasMore = inputReader.next(lineId, line);
			LOG.info("Url: " + line.toString());
			URL url = null;
			try {
				url = new URL(line.toString());
			} catch (Exception e) {
				LOG.error("Invalid Url: " + line.toString());
				continue;
			}

			HttpFetcher fetcher = new HttpFetcher(url);
			FetchContent content = null;
			try {
				content = fetcher.fetch();
			} catch (Exception e) {
				LOG.error("Fetch Content Failed.", e);
			}

			if (content != null) {
				LOG.info("Fetch Content"
						+ ", Response:" + content.getStatus() 
						+ ", Size:" + content.getContentSize());
				output.collect(line, content);
			}
		}

	}


	@Override
	public int run(String[] arg0) throws Exception {
		// 创建分布式爬虫任务
		JobConf job = new JobConf(getConf(), Crawl.class);
		job.setJobName("Sewing Crawl");

		// 设置输入
		submitUrlInput(job);

		// 设置输出
		submitCrawlOutput(job);

		// 设置分布式计算流程
		job.setMapRunnerClass(Crawl.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FetchContent.class);
		job.setNumMapTasks(15);
		//job.setReducerClass(TestReducer.class);
		job.setNumReduceTasks(0);
		
		JobClient.runJob(job);
		return 0;
	}

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed");
		Crawl crawl = context.getBean("crawl", Crawl.class);

		int res = ToolRunner.run(SewingConfiguration.create(), crawl, args);
		System.exit(res);
	}

}
