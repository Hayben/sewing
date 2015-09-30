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
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sidooo.ai.Keyword;
import com.sidooo.ai.Recognition;
import com.sidooo.content.HttpContent;
import com.sidooo.counter.CountService;
import com.sidooo.crawl.Filter;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.extractor.DocExtractor;
import com.sidooo.extractor.DocxExtractor;
import com.sidooo.extractor.ExtractorManager;
import com.sidooo.extractor.HtmlExtractor;
import com.sidooo.extractor.PdfExtractor;
import com.sidooo.point.Point;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;
import com.sidooo.senode.RedisConfiguration;


@Service("hbaseExtractor")
public class HbaseExtractor extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("HbaseExtractor");

	@Autowired
	private SeedService seedService;

	public static class ExtractMapper extends
			Mapper<Text, HttpContent, Keyword, Point> {

		private Recognition recognition;

		private List<Seed> seeds;
		
		private Filter filter ;
		
		private SeedService seedService;
		
		private int pointCount = 0;
		private int linkCount = 0;
		
		private int errNoExtractor = 0;
		private int errInput = 0;
		private int errRecognite = 0;
		private int errNoKeyword = 0;
		private int errTinyKey = 0;
		private int errSizeLimit = 0;
		private int errSkip = 0;
		
		private final long MAX_SIZE = 10 * 1024 * 1024; 
		private final long PDF_MAX_SIZE = 2 * 1024 * 1024;
		private final long DOC_MAX_SIZE = 2 * 1024 * 1024;
		private final long DOCX_MAX_SIZE = 2 * 1024 * 1024;
		private final long HTML_MAX_SIZE = 2 * 1024 * 1024;
		
		private ExtractorManager manager = new ExtractorManager();
		
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
			for(Seed seed : seeds) {
				seed.setPointCount(0);
				seed.setLinkCount(0);
			}
			
			recognition = new Recognition();
			
			filter = new Filter();
			
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class);
			appcontext.scan("com.sidooo.seed");
			seedService = appcontext.getBean("seedService",
					SeedService.class);
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
			context.getCounter("Sewing", "ERR_TINYKEY").increment(errTinyKey);
			context.getCounter("Sewing", "ERR_SIZE").increment(errSizeLimit);
			context.getCounter("Sewing", "ERR_SKIP").increment(errSkip);
			
			for(Seed seed : seeds) {
				seedService.incAnalysisStatistics(seed.getId(), seed.getPointCount(), seed.getLinkCount());
			}
		}

		@Override
		public void map(Text key, HttpContent fetch, Context context)
				throws IOException, InterruptedException {

			String url = key.toString();

			Seed seed = SeedService.getSeedByUrl(url, seeds);
			if (seed == null) {
				errSizeLimit ++;
				return;
			}
			
			if (fetch.getContentSize() >= MAX_SIZE) {
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
				extractor = manager.getInstanceByMime(mime);
			}

			// 根据后缀名识别出文件格式
			if (extractor == null) {
				String filename = fetch.getContentFilename();
				if (filename != null) {
					if (filter.accept(filename)) {
						extractor = manager.getInstanceByUrl(filename);
					} else {
						return;
					}
				}else {
					extractor = manager.getInstanceByUrl(url);
				}
			}

			// 根据内容识别文件格式
			if (extractor == null) {
				extractor = manager.getInstanceByContent(content);
			}

			if (extractor == null) {
				this.errNoExtractor ++ ;
				return;
			}
			
			LOG.info("Extractor:" + extractor.getClass().getName());
			if (extractor instanceof DocExtractor) {
//				if (content.length >= DOC_MAX_SIZE) {
//					this.errSizeLimit++;
//					extractor.close();
//					return;
//				}
				this.errSkip++;
				return;
			} else if (extractor instanceof DocxExtractor) {
//				if (content.length >= DOCX_MAX_SIZE) {
//					this.errSizeLimit++;
//					extractor.close();
//					return;
//				}
				this.errSkip++;
				return;
			} else if (extractor instanceof PdfExtractor) {
//				if (content.length >= PDF_MAX_SIZE) {
//					this.errSizeLimit ++;
//					extractor.close();
//					return;
//				}
				this.errSkip++;
				return;
			} else if (extractor instanceof HtmlExtractor) {
				if (content.length >= HTML_MAX_SIZE) {
					this.errSizeLimit ++;
					extractor.close();
					return;
				}
			} else {
				
			}
			

			extractor.setUrl(url);
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			try {
				extractor.setInput(input, null);
			} catch (Exception e1) {
				extractor.close();
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
					String word = keyword.getWord();
					if (word == null || word.length() < 6) {
						this.errTinyKey ++;
						continue;
					}
					context.write(keyword, point);
					linkCount ++;
					seed.incLinkCount();
				}

				pointCount ++;
				seed.incPointCount();
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
			TableReducer<Keyword, Point, ImmutableBytesWritable> {

		private Gson gson;
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			gson = new Gson();
		}
		
		@Override
		protected void reduce(Keyword keyword, Iterable<Point> values, Context context)
				throws IOException, InterruptedException {
			
			for(Point point: values) {
				
				Put putPoint = new Put(Bytes.toBytes(keyword.hash()));
				putPoint.add(Bytes.toBytes("points"),
						Bytes.toBytes(point.getDocId()), 
						Bytes.toBytes(gson.toJson(point)));
				context.write(null, putPoint);
				
				Put putKeyword = new Put(Bytes.toBytes(point.getDocId()));
				putKeyword.add(Bytes.toBytes("keywords"),
						Bytes.toBytes(keyword.hash()), Bytes.toBytes(gson.toJson(keyword)));
				context.write(null, putKeyword);
			}
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {

		Configuration conf = getConf();
		conf.setInt("mapreduce.map.cpu.vcores", 2);
		LOG.info("mapreduce.map.cpu.vcores : " + getConf().getInt("mapreduce.map.cpu.vcores", 1));
				
		conf.set("mapreduce.map.memory.mb", "2048");
		LOG.info("mapreduce.map.memory.mb : " + getConf().get("mapreduce.map.memory.mb"));
		
		conf.set("mapreduce.map.java.opts.max.heap", "1700");
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
		job.setMapOutputKeyClass(Keyword.class);
		job.setMapOutputValueClass(Point.class);

		TableMapReduceUtil.initTableReducerJob("wmouth", ExtractReducer.class,job);
		//TaskData.submitNullOutput(job);
		job.setNumReduceTasks(20);
		
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
			LOG.info("Create HBase Table: wmouth");
			HTableDescriptor table = new HTableDescriptor("wmouth");
			HColumnDescriptor columnPoint = new HColumnDescriptor(
					"points".getBytes());
			columnPoint.setMaxVersions(1);
			HColumnDescriptor columnLink = new HColumnDescriptor(
					"keywords".getBytes());
			columnLink.setMaxVersions(1);
			table.addFamily(columnPoint);
			table.addFamily(columnLink);
			hbase.createTable(table);
		}
		
		LOG.info("Create HBase Table wmouth successful.");

		hbase.close();

		LOG.info("Check Hbase finished.");

		int res = ToolRunner.run(conf, extractor, args);
		System.exit(res);
	}
}
