package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchStatus;
import com.sidooo.crawl.Filter;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.extractor.ContentDetector;
import com.sidooo.extractor.ContentType;
import com.sidooo.extractor.LinkExtractor;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("generator")
public class Generator extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("Generator");
	
	@Autowired
	private SeedService seedService;


	public static class GenerateMapper extends
			Mapper<Text, FetchContent, Text, FetchStatus> {

		private List<Seed> seeds;

		protected Filter filter = new Filter();

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			Configuration conf = context.getConfiguration();
			seeds = CacheLoader.loadSeedList(conf);
			if (seeds == null) {
				throw new InterruptedException("Seed List is null.");
			}
		}

		private String[] getLinks(String url, String charset, byte[] content) {
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			LinkExtractor extractor = new LinkExtractor();
			extractor.setUrl(url);
			try {
				extractor.setInput(input, charset);
				
				List<String> links = new ArrayList<String>();
				String line = null;
				while( (line = extractor.extract()) != null) {
					links.add(line);
				}
				
				return links.toArray(new String[links.size()]);
			} catch (Exception e) {
				return null;
			} finally {
				extractor.close();
			}

		}

		@Override
		protected void map(Text key, FetchContent value, Context context)
				throws IOException, InterruptedException {

			URL url = new URL(key.toString());
			LOG.info("Url: " + key.toString() + ",Charset: "
					+ value.getContentCharset());
			if (value.getStatus() == 200) {
				String contentType = value.getContentType();
				if (contentType == null || contentType.length() <= 0) {
					// ContentType存在Body中
					ContentDetector detector = new ContentDetector();
					ContentType type = detector.detect(value.getContent());
					if ("text/html".equals(type.mime)) {

						String charset = type.charset;
						if (charset.length() <= 0) {
							charset = "utf-8";
						}
						String[] links = getLinks(url.toString(), charset,
								value.getContent());
						for (String link : links) {
							if (filter.accept(link)) {
								FetchStatus status = new FetchStatus();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								context.write(new Text(link), status);
							}
						}
					}
				} else {
					// ContentType存在Response Header中
					if (contentType.equalsIgnoreCase("text/html")) {
						String charset = value.getContentCharset();
						if (charset == null || charset.length() <= 0) {
							charset = "utf-8";
						}
						String[] links = getLinks(url.toString(), charset,
								value.getContent());
						for (String link : links) {
							if (filter.accept(link)) {
								FetchStatus status = new FetchStatus();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								context.write(new Text(link), status);
							}
						}
					}
				}
			}

			if (filter.accept(url.toString())) {
				FetchStatus status = new FetchStatus();
				status.setStatus(value.getStatus());
				status.setFetchTime(value.getTimeStamp());
				context.write(key, status);
			}
		}
	}

	public static class GenerateReducer extends
			Reducer<Text, FetchStatus, Text, Text> {

		private List<Seed> seeds;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			Configuration conf = context.getConfiguration();
			seeds = CacheLoader.loadSeedList(conf);
			if (seeds == null) {
				throw new InterruptedException("Seed List is null.");
			}
		}
		
		@Override
		protected void reduce(Text key, Iterable<FetchStatus> values,
				Context context) throws IOException, InterruptedException {
			
			List<FetchStatus> list = new ArrayList<FetchStatus>();
			for(FetchStatus fetch : values) {
				list.add(fetch);
			}
			
			Collections.sort(list);
			
			Seed seed = SeedService.getSeedByUrl(key.toString(), seeds);
			if (seed == null) {
				return;
			}
			
			UrlStatus status = UrlStatus.from(values);
			if (status == UrlStatus.READY) {
				context.getCounter("Sewing", "URL").increment(1);
				context.write(key, new Text(seed.getId()));
			}
		}

	}

	@Override
	public int run(String[] arg0) throws Exception {

		// LOG.info("ZooKeeper:" + getConf().get("hbase.zookeeper.quorum"));

		Job job = new Job(getConf());
		job.setJobName("Sewing Generator");
		job.setJarByClass(Generator.class);

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		if (seeds.size() <= 0) {
			LOG.warn("No Seed to fetch.");
			return 1;
		} else {
			LOG.info("Seed Count: " + seeds.size());
		}
		CacheSaver.submitSeedCache(job, seeds);

		// 设置输入的数据源
		TaskData.submitSeedInput(job, seeds);
		int count = TaskData.submitCrawlInput(job);
		LOG.info("Crawl File Count: " + count);

		// 设置输出
		TaskData.submitUrlOutput(job);

		// 设置计算流程
		job.setMapperClass(GenerateMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FetchStatus.class);
		job.setReducerClass(GenerateReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(30);

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			long urlCount = group.findCounter("URL").getValue();
			System.out.println("URL Count: " + urlCount);
			return 0;
		} else {
			return 1;
		}

		//
		// FSDataInputStream hadoopStream = fs.open(outPath);
		//
		//
		// BufferedReader in = null;
		// try {
		// in = new BufferedReader(
		// new InputStreamReader(hadoopStream, "UTF-8"));
		// String line = "";
		// while((line = in.readLine()) != null) {
		// queue.sendUrl(line);
		// }
		// } catch(Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (in != null) {
		// in.close();
		// }
		// }
		//
	}

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.sewing");
		Generator generator = context.getBean("generator", Generator.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), generator, args);
		System.exit(res);
	}
}
