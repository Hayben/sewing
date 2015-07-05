package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.ai.Keyword;
import com.sidooo.ai.Recognition;
import com.sidooo.counter.CountService;
import com.sidooo.crawl.FetchContent;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.point.Item;
import com.sidooo.point.Link;
import com.sidooo.point.LinkRepository;
import com.sidooo.point.Point;
import com.sidooo.point.PointRepository;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;
import com.sidooo.senode.RedisConfiguration;

@Service("mongoExtractor")
public class MongoExtractor extends SewingConfigured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("MongoExtractor");

	@Autowired
	private SeedService seedService;

	@Autowired
	private CountService countService;

	public static class ExtractMapper extends
			Mapper<Text, FetchContent, Keyword, Text> {

		private Recognition recognition;

		private List<Seed> seeds;

		private PointRepository pointRepo;

		private CountService countService;

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

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class, RedisConfiguration.class);
			appcontext.scan("com.sidooo.point", "com.sidooo.counter");
			pointRepo = appcontext.getBean("pointRepository",
					PointRepository.class);

			countService = appcontext.getBean("countService",
					CountService.class);

			pointRepo.clear();
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
			} catch (Exception e) {
				LOG.error("Extract " + url + " Fail.", e);
				return;
			}
			List<Item> items = extractor.getItems();
			LOG.info(url + ", Extractor:" + extractor.getClass().getName() + ", Item Count:"
					+ items.size());
			for (Item item : items) {

				if (item.getContentSize() <= 0) {
					LOG.warn("Item:" + item.getId() +" Content NULL");
					continue;
				}

				Keyword[] keywords = null;
				try {
					keywords = recognition.search(item.getContent());
				} catch (Exception e) {
					LOG.error("Item:" + item.getId() +" Recognite Fail.", e);
					continue;
				}

				if (keywords == null || keywords.length <= 0) {
					LOG.warn("Item:" + item.getId() +" Keyword not found.");
					continue;
				}

				Point point = new Point();
				point.setDocId(item.getId());
				point.setTitle(seed.getName());
				point.setUrl(url);
				for (Keyword keyword : keywords) {
					point.addLink(keyword);
					context.write(keyword, new Text(point.getDocId()));
					countService.incLinkCount(seed.getId());
				}

				pointRepo.createPoint(point);
				countService.incPointCount(seed.getId());

				LOG.info(point.toString());
				context.getCounter("Sewing", "Point").increment(1);
			}
		}
	}

	public static class ExtractReducer extends
			Reducer<Keyword, Text, NullWritable, NullWritable> {

		private LinkRepository linkRepo;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class, RedisConfiguration.class);
			appcontext.scan("com.sidooo.point", "com.sidooo.counter");
			linkRepo = appcontext.getBean("linkRepository",
					LinkRepository.class);

			linkRepo.clear();
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {

		}

		@Override
		protected void reduce(Keyword keyword, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {

			Link link = new Link();
			link.setKeyword(keyword.getWord());
			link.setType(keyword.getAttr());

			Iterator<Text> points = values.iterator();
			if (points.hasNext()) {
				Text point = points.next();
				link.addPoint(point.toString());
			}

			linkRepo.createLink(link);

			LOG.info(link.toString());
			context.getCounter("Sewing", "Link").increment(1);
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {

		Job job = new Job(getConf());
		job.setJobName("Sewing Extract");
		job.setJarByClass(MongoExtractor.class);

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		CacheSaver.submitSeedCache(job, seeds);
		CacheSaver.submitNlpCache(job);

		// 设置输入
		//TaskData.submitCrawlInput(job);
		TaskData.SubmitThreeCrawlInput(job);

		// 设置输出
		// TaskData.submitPointOutput(job);

		// 设置计算流程
		job.setMapperClass(ExtractMapper.class);
		job.setMapOutputKeyClass(Keyword.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(ExtractReducer.class);
		TaskData.submitNullOutput(job);
		job.setNumReduceTasks(10);

		for (Seed seed : seeds) {
			countService.resetCount(seed.getId());
		}

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			long pointCount = group.findCounter("Point").getValue();
			System.out.println("Point Count: " + pointCount);
			long linkCount = group.findCounter("Link").getValue();
			System.out.println("Link Count: " + linkCount);

			for (Seed seed : seeds) {
				long seedPointCount = countService.getPointCount(seed.getId());
				long seedLinkCount = countService.getLinkCount(seed.getId());

				seedService.updateAnalysisStatistics(seed.getId(),
						seedPointCount, seedLinkCount);
			}
			return 0;
		} else {
			return 1;
		}

	}

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class, RedisConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		MongoExtractor extractor = context.getBean("mongoExtractor",
				MongoExtractor.class);

		int res = ToolRunner.run(SewingConfiguration.create(), extractor, args);
		System.exit(res);
	}
}