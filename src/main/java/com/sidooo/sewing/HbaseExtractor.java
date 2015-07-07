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
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
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
import com.sidooo.counter.CountService;
import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.Filter;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.point.Point;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;


@Service("hbaseExtractor")
public class HbaseExtractor extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("HbaseExtractor");

	@Autowired
	private SeedService seedService;

	public static class ExtractMapper extends
			Mapper<Text, FetchContent, Text, Point> {

		private Recognition recognition;

		private List<Seed> seeds;
		
		private Filter filter ;
		
		private CountService countService;
		
		private int pointCount = 0;
		private int linkCount = 0;
		
		private int errNoExtractor = 0;
		private int errInput = 0;
		private int errRecognite = 0;
		private int errNoKeyword = 0;
		
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
			
			filter = new Filter();
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {
			context.getCounter("Sewing", "Point").increment(pointCount);
			context.getCounter("Sewing", "Link").increment(linkCount);
			context.getCounter("Sewing", "ERR_NOEXTRACTOR").increment(errNoExtractor);
			context.getCounter("Sewing", "ERR_INPUT").increment(errInput);
			context.getCounter("Sewing", "ERR_NOKEYWORD").increment(errNoKeyword);
			context.getCounter("Sewing", "ERR_RECOGNITE").increment(errRecognite);
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
			String mime = fetch.getContentType();
			
			LOG.info("Url: " + url 
					+ ", Response: " + fetch.getStatus() 
					+ ", ReadSize:" + content.length
					+ ", FetchSize:" + fetch.getContentSize()
					+ ", Mime:" + mime);
			
			if (fetch.getStatus() != 200) {
				return;
			}
			
			// 根据爬虫的应答头部识别文件格式
			if (mime != null && mime.length() > 0) {
				extractor = ContentExtractor.getInstanceByMime(mime);
			}

			// 根据后缀名识别出文件格式
			if (extractor == null) {
				String filename = fetch.getContentFilename();
				if (filename != null) {
					Filter filter = new Filter();
					if (filter.accept(filename)) {
						extractor = ContentExtractor.getInstanceByUrl(filename);
					} else {
						return;
					}
				}else {
					extractor = ContentExtractor.getInstanceByUrl(url);
				}
			}

			// 根据内容识别文件格式
			if (extractor == null) {
				extractor = ContentExtractor.getInstanceByContent(content);
			}

			if (extractor == null) {
				this.errNoExtractor ++ ;
				return;
			}
			
			LOG.info("Extractor:" + extractor.getClass().getName());

			extractor.setUrl(url);
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			try {
				extractor.setInput(input, null);
			} catch (Exception e1) {
				this.errInput ++;
				return;
			}

			int itemCount = 0;
			Point point = new Point();
			String item = null;
			while( (item = extractor.extract()) != null) {

				itemCount ++;
				
				if (item.length() <= 0) {
					continue;
				}

				Keyword[] keywords = null;
				try {
					keywords = recognition.search(item);
				} catch (Exception e) {
					this.errRecognite ++;
					continue;
				}

				if (keywords == null || keywords.length <= 0) {
					this.errNoKeyword ++;
					continue;
				}

				point.clear();
				point.setDocId(Point.md5(item));
				point.setTitle(seed.getName());
				point.setUrl(url);
				for (Keyword keyword : keywords) {
					point.addLink(keyword);
					linkCount ++;
				}

				pointCount ++;
				context.write(new Text(point.getDocId()), point);
				//LOG.info(point.toString());

			}
			
			extractor.close();
//			countService.incLinkCount(seed.getId(), linkCount);
//			countService.incPointCount(seed.getId(), pointCount);
			
			LOG.info("Extractor:" + extractor.getClass().getName() 
					+ ", Item Count:" + itemCount);

		}
	}


	public static class ExtractReducer extends
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
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {

		Configuration conf = getConf();
		conf.setInt("mapreduce.map.cpu.vcores", 4);
		LOG.info("mapreduce.map.cpu.vcores : " + getConf().getInt("mapreduce.map.cpu.vcores", 1));
				
		conf.set("mapreduce.map.memory.mb", "2048");
		LOG.info("mapreduce.map.memory.mb : " + getConf().get("mapreduce.map.memory.mb"));
		
		conf.set("mapreduce.map.java.opts.max.heap", "1536");
		LOG.info("mapreduce.map.java.opts.max.heap : " + getConf().get("mapreduce.map.java.opts.max.heap"));
		
		Job job = new Job(getConf());
		job.setJobName("Sewing Extract");
		job.setJarByClass(HbaseExtractor.class);
		
		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		CacheSaver.submitSeedCache(job, seeds);
		CacheSaver.submitNlpCache(job);

		// 设置输入
		TaskData.submitCrawlInput(job);

		// 设置计算流程
		job.setMapperClass(ExtractMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Point.class);

		TableMapReduceUtil.initTableReducerJob("wmouth", ExtractReducer.class,job);
		TaskData.submitNullOutput(job);
		job.setNumReduceTasks(15);
		
		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			Counter counter = group.findCounter("Point");
			long pointCount = counter.getValue();
			System.out.println("Point Count: " + pointCount);
			counter = group.findCounter("Link");
			long linkCount = counter.getValue();
			System.out.println("Link Count: " + linkCount);
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
