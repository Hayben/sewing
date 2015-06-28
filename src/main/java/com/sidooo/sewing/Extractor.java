package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.FetchContent;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.point.Item;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.DatawareConfiguration;

@Service("extractor")
public class Extractor extends SewingConfigured implements Tool {

	@Autowired
	private SeedService seedService;

	public static class ExtractMapper extends SewingMapReduce implements
			Mapper<Text, FetchContent, Text, Item> {

		private Tika tika = new Tika();

		@Override
		public void configure(JobConf job) {
			checkCacheFiles(job);
		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub

		}

		@Override
		public void map(Text key, FetchContent fetch,
				OutputCollector<Text, Item> output, Reporter reporter)
				throws IOException {

			String url = key.toString();
			Seed seed = getSeedByUrl(url);
			if (seed == null) {
				return;
			}

			byte[] content = fetch.getContent();

			ContentExtractor extractor = ContentExtractor.getInstance(url);
			if (extractor != null) {
				ByteArrayInputStream input = new ByteArrayInputStream(content);
				extractor.extract(input);
				List<Item> items = extractor.getItems();
				for (Item item : items) {
					output.collect(new Text(item.getId()), item);
				}
			}
		}
	}

	public static class ExtractReducer extends SewingMapReduce implements
			Reducer<Text, Item, Text, Item> {

		@Override
		public void reduce(Text key, Iterator<Item> values,
				OutputCollector<Text, Item> output, Reporter reporter)
				throws IOException {

			String url = key.toString();
			if (values.hasNext()) {
				Item item = values.next();
				output.collect(key, item);
			}
		}

	}

	@Override
	public int run(String[] arg0) throws Exception {

		JobConf job = new JobConf(getConf(), Extractor.class);
		job.setJobName("Sewing Extract");

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		submitSeedCache(job, seeds);

		// 设置输入
		submitCrawlInput(job);

		// 设置输出
		submitItemOutput(job);

		// 设置计算流程
		job.setMapperClass(ExtractMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Item.class);
		job.setReducerClass(ExtractReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Item.class);

		JobClient.runJob(job);
		return 0;
	}

	public static void main(String args[]) throws Exception {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		Extractor Extractor = context.getBean("extractor", Extractor.class);

		int res = ToolRunner.run(SewingConfiguration.create(), Extractor, args);
		System.exit(res);
	}
}