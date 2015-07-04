package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
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

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchStatus;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.extractor.ContentDetector;
import com.sidooo.extractor.ContentType;
import com.sidooo.extractor.HtmlExtractor;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("generator")
public class Generator extends SewingConfigured implements Tool {


	@Autowired
	private SeedService seedService;
	
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String WAIT = "wait";

	public Generator() {

	}

	public static class GenerateMapper extends SewingMapReduce implements
			Mapper<Text, FetchContent, Text, FetchStatus> {

		@Override
		public void configure(JobConf conf) {
			checkCacheFiles(conf);
		}

		private String[] getLinks(String url, String charset, byte[] content) {
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			HtmlExtractor extractor = new HtmlExtractor();
			extractor.setUrl(url);
			extractor.extractLink(input, charset);
			String[] links = extractor.getLinks();
			return links;
		}

		@Override
		public void map(Text key, FetchContent value,
				OutputCollector<Text, FetchStatus> output, Reporter reporter)
				throws IOException {

			URL url = new URL(key.toString());
			LOG.info("Url: " + key.toString() + ",Charset: "
					+ value.getCharset());
			if (value.getStatus() == 200) {

				if (value.getMime().length() > 0) {
					// ContentType存在Response Header中
					if (value.getMime().equalsIgnoreCase("text/html")) {
						String charset = value.getCharset();
						if (charset.length() <= 0) {
							charset = "utf-8";
						}
						String[] links = getLinks(url.toString(),
								charset, value.getContent());
						for (String link : links) {
							if (accept(link)) {
								FetchStatus status = new FetchStatus();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								output.collect(new Text(link), status);
							}
						}
					}
				} else {
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
							if (accept(link)) {
								FetchStatus status = new FetchStatus();
								status.setStatus(1);
								status.setFetchTime(value.getTimeStamp());
								output.collect(new Text(link), status);
							}
						}
					}

				}

			}

			if (accept(url.toString())) {
				FetchStatus status = new FetchStatus();
				status.setStatus(value.getStatus());
				status.setFetchTime(value.getTimeStamp());
				output.collect(key, status);
			}
		}

		public void close() throws IOException {

		}
	}

	public static class GenerateReducer extends SewingMapReduce implements
			Reducer<Text, FetchStatus, Text, NullWritable> {
		
		@Override
		public void configure(JobConf conf) {
			checkCacheFiles(conf);
		}
		
		@Override
		public void reduce(Text key, Iterator<FetchStatus> values,
				OutputCollector<Text, NullWritable> output, Reporter reporter)
				throws IOException {

			Seed seed = getSeedByUrl(key.toString());
			if (seed == null) {
				return;
			}
			
			String url = key.toString();
			
			UrlStatus status = new UrlStatus(values);

			if (status.hasSucceed()) {
				if (status.hasExpired()) {
					LOG.info("Add Url: " + url);
					output.collect(key, NullWritable.get());
				}
			} else {
				if (status.hasSizeLimit()) {
					//文件太大， 放弃
				} else {
					if (status.hasRetryLimit()) {
						//重试次数已经到达阀值， 放弃
					} else {
						LOG.info("Add Url: " + url);
						output.collect(key, NullWritable.get());
					}
				}
			}

		}

	}

	@Override
	public int run(String[] arg0) throws Exception {

		// LOG.info("ZooKeeper:" + getConf().get("hbase.zookeeper.quorum"));

		JobConf job = new JobConf(getConf(), Generator.class);
		job.setJobName("Sewing Generator");

		// 设置缓存
		List<Seed> seeds = seedService.getEnabledSeeds();
		if (seeds.size() <= 0) {
			LOG.warn("No Seed to fetch.");
			return 0;
		}
		submitSeedCache(job, seeds);

		// 设置输入的数据源
		submitSeedInput(job, seeds);
		int count = submitCrawlInput(job);
		LOG.info("Crawl File Count: " + count);

		// 设置输出
		Path urlFile = submitUrlOutput(job);

		// 设置计算流程
		job.setMapperClass(GenerateMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FetchStatus.class);
		job.setReducerClass(GenerateReducer.class);
		job.setNumReduceTasks(30);

		JobClient.runJob(job);
		LOG.info("Output Size:" + getFileSize(urlFile));
		
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
		return 0;
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
