package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchRecord;
import com.sidooo.crawl.FetchResult;
import com.sidooo.crawl.Filter;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.extractor.ContentDetector;
import com.sidooo.extractor.ContentType;
import com.sidooo.extractor.LinkExtractor;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;


@Service("urldb")
public class UrlDatabase extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("urldb");

	@Autowired
	private SeedService seedService;

	public static class FetchContentMapper extends
			Mapper<Text, FetchContent, FetchRecord, FetchResult> {

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

				Set<String> links = new HashSet<String>();
				String line = null;
				while ((line = extractor.extract()) != null) {
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

								FetchResult status = new FetchResult();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								context.write(
										new FetchRecord(link, value
												.getTimeStamp()), status);
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
								FetchResult status = new FetchResult();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								context.write(
										new FetchRecord(link, value
												.getTimeStamp()), status);
							}
						}
					}
				}
			}

			if (filter.accept(url.toString())) {
				FetchResult status = new FetchResult();
				status.setStatus(value.getStatus());
				status.setFetchTime(value.getTimeStamp());
				context.write(
						new FetchRecord(key.toString(), value.getTimeStamp()),
						status);
			}
		}
	}

	public static class UrlPartitioner extends
			Partitioner<FetchRecord, FetchResult> {

		@Override
		public int getPartition(FetchRecord key, FetchResult value,
				int numPartitions) {
			
//			LOG.info("Partition URL: " + key.getUrl());
			URI uri = null;
			try {
				uri = new URI(key.getUrl());
			} catch (URISyntaxException e) {
				LOG.info("Invalid URL:" + key.getUrl());
				return 0;
			}
			String host = uri.getHost();
			if (host == null) {
				LOG.info("Unknown Host:" + host);
				return 0;
			}
			return (host.hashCode() &Integer.MAX_VALUE) % numPartitions;
		}

	}

	public static class GroupingComparator extends WritableComparator{

		public GroupingComparator() {
			super(FetchRecord.class, true);
		}
		
		
		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			
			FetchRecord r1 = (FetchRecord)a;
			FetchRecord r2 = (FetchRecord)b;
			
			String u1 = r1.getUrl();
			String u2 = r2.getUrl();
			if (u1.equalsIgnoreCase(u2)) {
				return 0;
			} else {
				return u1.compareToIgnoreCase(u2);
			}
		}
	}

	public static class SortingComparator extends WritableComparator {

		public SortingComparator() {
			super(FetchRecord.class, true);
		}

		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			FetchRecord r1 = (FetchRecord)a;
			FetchRecord r2 = (FetchRecord)b;
			
			int ret = r1.getUrl().compareToIgnoreCase(r2.getUrl());
			if (ret != 0) {
				return ret;
			} else {
				long ts1 = r1.getStamp();
				long ts2 = r2.getStamp();
				if (ts1 == ts2) {
					return 0;
				} else {
					return ts1 < ts2 ? 1 : -1;
				}
			}
		}
	}

	public static class FetchResultReducer extends
			Reducer<FetchRecord, FetchResult, Text, IntWritable> {

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

		private void printList(Iterable<FetchResult> l) {
			StringBuilder builder = new StringBuilder();
			for (FetchResult status : l) {
				builder.append(status.getFetchTime() + ":" + status.getStatus()
						+ ",");
			}

			LOG.info(builder.toString());
		}

		@Override
		protected void reduce(FetchRecord key, Iterable<FetchResult> values,
				Context context) throws IOException, InterruptedException {

			UrlStatus status;
			
			Seed seed = SeedService.getSeedByUrl(key.getUrl(), seeds);
			if (seed == null) {
				status = UrlStatus.FILTERED;
			} else {
				status = UrlStatus.from(values);
			}
			
//			printList(values);
			
//			List<FetchResult> list = new ArrayList<FetchResult>();
//			for (FetchResult fetch : values) {
//				list.add(fetch);
//			}
//
//			Collections.sort(list);
//			printList(list);

			
			LOG.info(key.getUrl() + ":" + status);
			context.write(new Text(key.getUrl()), new IntWritable(status.getValue()));
			
			context.getCounter("Sewing", "URL").increment(1);
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
		Path[] crawlFiles = TaskData.submitCrawlInput(job);
		LOG.info("Crawl File Count: " + crawlFiles.length);

		// 设置输出
		TaskData.submitUrldbOutput(job);

		// 设置计算流程
		job.setMapperClass(FetchContentMapper.class);
		job.setMapOutputKeyClass(FetchRecord.class);
		job.setMapOutputValueClass(FetchResult.class);
		
		job.setPartitionerClass(UrlPartitioner.class);
		job.setGroupingComparatorClass(GroupingComparator.class);
		job.setSortComparatorClass(SortingComparator.class);
		
		job.setReducerClass(FetchResultReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(1);

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
		UrlDatabase urldb = context.getBean("urldb", UrlDatabase.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), urldb, args);
		System.exit(res);
	}
}
