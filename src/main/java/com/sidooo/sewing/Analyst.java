package com.sidooo.sewing;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
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

import com.sidooo.ai.IDKeyword;
import com.sidooo.ai.Recognition;
import com.sidooo.point.Item;
import com.sidooo.point.Link;
import com.sidooo.point.Point;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.DatawareConfiguration;

@Service("analyst")
public class Analyst extends SewingConfigured implements Tool {

	@Autowired
	private SeedService seedService;

	public static class PointMapper extends SewingMapReduce implements
			Mapper<Text, Item, Text, Point> {

		private final int MIN_CONTENT_LEN = 4;
		private Recognition recognition ;

		@Override
		public void configure(JobConf conf) {
			checkCacheFiles(conf);
			recognition = new Recognition();
		}

		@Override
		public void map(Text key, Item item,
				OutputCollector<Text, Point> output, Reporter reporter)
				throws IOException {
			String content = item.getContent();
			if (content == null || content.length() <= MIN_CONTENT_LEN) {
				return;
			}
			
			IDKeyword[] keywords = recognition.search(item.getContent());
			if (keywords == null || keywords.length <= 0) {
				return;
			}
			Point point = new Point();
			point.setDocId(item.getId());
			point.setTitle(item.getTitle());
			for (IDKeyword keyword : keywords) {
				point.addLink(keyword.word);
			}
			output.collect(key, point);
		}

	}

	@Override
	public int run(String[] arg0) throws Exception {
		JobConf job = new JobConf(getConf(), Analyst.class);

		job.setJobName("Sewing Analysis");

		List<Seed> seeds = seedService.getEnabledSeeds();
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
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		Analyst crawl = context.getBean("analyst", Analyst.class);

		int res = ToolRunner.run(SewingConfiguration.create(), crawl, args);
		System.exit(res);
	}
}
