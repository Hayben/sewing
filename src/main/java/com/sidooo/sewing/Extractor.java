package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.ai.Keyword;
import com.sidooo.ai.Recognition;
import com.sidooo.crawl.FetchContent;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.point.Item;
import com.sidooo.point.Point;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.DatawareConfiguration;
import com.sidooo.senode.MongoConfiguration;

@Service("extractor")
public class Extractor extends SewingConfigured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("Extractor");

	@Autowired
	private SeedService seedService;

	public static class ExtractMapper extends
			Mapper<Text, FetchContent, Text, Point> {

		private final int MIN_CONTENT_LEN = 4;
		private Recognition recognition;

		private List<Seed> seeds;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			Configuration conf = context.getConfiguration();
			seeds = CacheLoader.loadSeedList(conf);
			if (seeds == null) {
				throw new InterruptedException("Seed List is null.");
			}
			if (!CacheLoader.exiistHanlpData(conf)) {
				throw new InterruptedException("Hanlp Data Archive not found.");
			}
			if (!CacheLoader.existHanlpJar(conf)) {
				throw new InterruptedException("Hanlp Jar not found.");
			}
			recognition = new Recognition();
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {

		}

		@Override
		public void map(Text key, FetchContent fetch, Context context)
				throws IOException, InterruptedException {

			String url = key.toString();

			Seed seed = SeedService.getSeedByUrl(url, seeds);
			if (seed == null) {
				return;
			}

			byte[] content = fetch.getContent();
			if (content == null || content.length < 8) {
				return;
			}

			ContentExtractor extractor = null;
			String mime = fetch.getMime();

			// 根据爬虫的应答头部识别文件格式
			if (mime != null && mime.length() > 0) {
				extractor = ContentExtractor.getInstanceByMime(mime);
			}

			// 根据后缀名识别出文件格式
			if (extractor == null) {
				extractor = ContentExtractor.getInstanceByUrl(url);
			}

			// 根据内容识别文件格式
			if (extractor == null) {
				extractor = ContentExtractor.getInstanceByContent(content);
			}

			if (extractor == null) {
				LOG.warn("Unknown File Format, Url: " + url + ", Content Size:"
						+ content.length);
				return;
			}

			extractor.setUrl(url);
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			try {
				extractor.extract(input);
			} catch(Exception e) {
				LOG.error("Extract " + url + " Fail.", e);
				return;
			}
			List<Item> items = extractor.getItems();
			LOG.info("Url:" + url + ", Extractor:"
					+ extractor.getClass().getName() + ", Item Count:"
					+ items.size());
			for (Item item : items) {

				if (item.getContentSize() <= 0) {
					LOG.warn("Content NULL, Url: " + url);
					continue;
				}

				Keyword[] keywords = null;
				try {
					keywords = recognition.search(item.getContent());
				} catch (Exception e) {
					LOG.error("Recognite Fail.", e);
					continue;
				}

				if (keywords == null || keywords.length <= 0) {
					continue;
				}

				Point point = new Point();
				point.setDocId(item.getId());
				point.setTitle(seed.getName());
				point.setUrl(url);
				for (Keyword keyword : keywords) {
					point.addLink(keyword);
				}
				LOG.info("PointId:" + point.getDocId() + ", Keyword Count:"
						+ point.getLinks().length);

				context.write(new Text(point.getDocId()), point);
			}

		}
	}

	public static class ExtractReducer extends
			Reducer<Text, Point, Text, Point> {

		@Override
		protected void reduce(Text key, Iterable<Point> values,
				Context context) throws IOException, InterruptedException {
			
			Iterator<Point> it = values.iterator();
			if (it.hasNext()) {
				Point point = it.next();
				context.write(key, point);
				context.getCounter("Sewing", "Point").increment(1);
			}
		}

	}

	@Override
	public int run(String[] arg0) throws Exception {

		Job job = new Job(getConf());
		job.setJobName("Sewing Extract");
		job.setJarByClass(Extractor.class);

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		CacheSaver.submitSeedCache(job, seeds);
		CacheSaver.submitNlpCache(job);

		// 设置输入
		TaskData.submitCrawlInput(job);

		// 设置输出
		TaskData.submitPointOutput(job);

		// 设置计算流程
		job.setMapperClass(ExtractMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Point.class);
		job.setReducerClass(ExtractReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Point.class);

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			Counter counter = group.findCounter("Point");
			long pointCount = counter.getValue();
			System.out.println("Point Count: " + pointCount);
			return 0;
			
		} else {
			return 1;
		}
		
	}

	public static void main(String args[]) throws Exception {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		Extractor Extractor = context.getBean("extractor", Extractor.class);

		int res = ToolRunner.run(SewingConfiguration.create(), Extractor, args);
		System.exit(res);
	}
}
