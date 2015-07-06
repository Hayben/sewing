package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
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


@Service("hbaseExtractor")
public class HbaseExtractor extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("HbaseExtractor");

	@Autowired
	private SeedService seedService;

	public static class ExtractMapper extends
			Mapper<Text, FetchContent, Keyword, Text> {

		private Recognition recognition;

		private List<Seed> seeds;
		
		private PointRepository pointRepo;

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
					MongoConfiguration.class);
			appcontext.scan("com.sidooo.point");
			pointRepo = appcontext.getBean("pointRepository", PointRepository.class);
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
					LOG.warn("Keyword not found.");
					continue;
				}

				Point point = new Point();
				point.setDocId(item.getId());
				point.setTitle(seed.getName());
				point.setUrl(url);
				for (Keyword keyword : keywords) {
					point.addLink(keyword);
					context.write(keyword, new Text(point.getDocId()));
				}

				pointRepo.createPoint(point);

				//context.write(new Text(point.getDocId()), point);
				
				LOG.info("PointId:" + point.getDocId() + ", Keyword Count:"
						+ point.getLinks().length);
				context.getCounter("Sewing", "Point").increment(1);
			}

		}
	}

	public static class ExtractReducer extends
			Reducer<Text, Point, Text, Point> {

		@Override
		protected void reduce(Text key, Iterable<Point> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Point> it = values.iterator();
			if (it.hasNext()) {
				Point point = it.next();
				context.write(key, point);
				context.getCounter("Sewing", "Point").increment(1);
			}
		}
	}
	
	public static class ExtractReducerToMongo extends 
		Reducer<Keyword, Text, NullWritable, NullWritable> {
			
		
		private LinkRepository linkRepo;
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class);
			appcontext.scan("com.sidooo.point");
			linkRepo = appcontext.getBean("linkRepository", LinkRepository.class);
			linkRepo.clear();
		}
		
		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {

		}
		
		@Override
		protected void reduce(Keyword keyword, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			
			Link link = new Link();
			link.setKeyword(keyword.getWord());
			link.setType(keyword.getAttr());
			
			Iterator<Text> points = values.iterator();
			if (points.hasNext()) {
				Text point = points.next();
				link.addPoint(point.toString());
			}
			
			linkRepo.createLink(link);
			
			context.getCounter("Sewing", "Link").increment(1);
		}
	}

	public static class ExtractReducer2 extends
			TableReducer<Text, Point, ImmutableBytesWritable> {

		@Override
		protected void reduce(Text key, Iterable<Point> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Point> it = values.iterator();
			if (!it.hasNext()) {
				return;
			}

			Point point = it.next();

			Keyword[] links = point.getLinks();
			for (Keyword link : links) {

				LOG.info("Point Id:" + point.getDocId() + ", Keyword:" + link.getWord());

				String rowkey1 = key.toString() + "_" + link.hash();
				Put put1 = new Put(Bytes.toBytes(rowkey1));
				put1.add(Bytes.toBytes("content"),
						Bytes.toBytes("type"), Bytes.toBytes("point"));
				put1.add(Bytes.toBytes("content"),
						Bytes.toBytes("title"), Bytes.toBytes(point.getTitle()));
				put1.add(Bytes.toBytes("content"),
						Bytes.toBytes("url"), Bytes.toBytes(point.getUrl()));
				context.write(null, put1);

				String rowkey2 = link.hash() + "_" + key.toString();
				Put put2 = new Put(Bytes.toBytes(rowkey2));
				put2.add(Bytes.toBytes("content"),
						Bytes.toBytes("type"), Bytes.toBytes("link"));
				put2.add(Bytes.toBytes("content"),
						Bytes.toBytes("word"), Bytes.toBytes(link.getWord()));
				put2.add(Bytes.toBytes("content"),
						Bytes.toBytes("attr"), Bytes.toBytes(link.getAttr()));
				context.write(null, put2);
			}

			context.getCounter("Sewing", "Point").increment(1);
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {

		Job job = new Job(getConf());
		job.setJobName("Sewing Extract");
		job.setJarByClass(HbaseExtractor.class);

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		CacheSaver.submitSeedCache(job, seeds);
		CacheSaver.submitNlpCache(job);

		// 设置输入
		TaskData.SubmitTestCrawlInput(job);

		// 设置输出
		// TaskData.submitPointOutput(job);

		// 设置计算流程
		job.setMapperClass(ExtractMapper.class);
		job.setMapOutputKeyClass(Keyword.class);
		job.setMapOutputValueClass(Text.class);

//		TableMapReduceUtil.initTableReducerJob("wmouth", ExtractReducer2.class,
//				job);
//		job.setNumReduceTasks(10);
		
		 job.setReducerClass(ExtractReducerToMongo.class);
//		 job.setOutputKeyClass(Text.class);
//		 job.setOutputValueClass(Point.class);
//		 
		TaskData.submitNullOutput(job);
		job.setNumReduceTasks(10);
		
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

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		HbaseExtractor extractor = context.getBean("hbaseExtractor", HbaseExtractor.class);

		LOG.info("Load HBase Configuration");
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum",
				"node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		LOG.info("HBase Zookeeper Quorum: "
				+ conf.get("hbase.zookeeper.quorum"));

		HBaseAdmin hbase = new HBaseAdmin(conf);

		if (!hbase.tableExists("wmouth")) {
			LOG.info("Create HBase Table: crawl");
			HTableDescriptor table = new HTableDescriptor("wmouth");
			HColumnDescriptor column = new HColumnDescriptor(
					"content".getBytes());

			table.addFamily(column);
			hbase.createTable(table);

			LOG.info("Create HBase Table wmouth successful.");
		} else {
			HTableDescriptor table = hbase.getTableDescriptor("wmouth"
					.getBytes());

			if (!table.hasFamily("content".getBytes())) {
				HColumnDescriptor column = new HColumnDescriptor(
						"content".getBytes());
				table.addFamily(column);

				LOG.info("Add Column Family: keywords");
			}
		}

		hbase.close();

		LOG.info("Check Hbase finished.");

		int res = ToolRunner.run(conf, extractor, args);
		System.exit(res);
	}
}
