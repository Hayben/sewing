package com.sidooo.sewing;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

import com.sidooo.crawl.UrlStatus;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

public class SeedGenerator extends Configured implements Tool{

	public static final Logger LOG = LoggerFactory.getLogger("SeedGenerator");
	
	@Autowired
	private SeedService seedService;

	public static class UrlMapper extends
			Mapper<Text, IntWritable, Text, NullWritable> {

		private List<Seed> seeds;
		
		@Override
		public void map(Text key, IntWritable value, Context context)
				throws IOException, InterruptedException {

			String url = key.toString();
			
			UrlStatus status = UrlStatus.valueOf(value.get());
			
			if (status == UrlStatus.FILTERED 
					&& SeedService.getSeedByUrl(url, seeds) == null) {
				
				URI uri;
				try {
					uri = new URI(url);
				} catch (URISyntaxException e) {
					return;
				}
				String seedUrl = uri.getScheme() + uri.getHost();
				context.write(new Text(seedUrl), NullWritable.get());
			}

		}
	}

	public static class ReadyUrlReducer extends
			Reducer<Text, NullWritable, Text, NullWritable> {

		private SeedService seedService; 
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext appcontext = new AnnotationConfigApplicationContext(
					MongoConfiguration.class);
			appcontext.scan("com.sidooo.seed");
			seedService = appcontext.getBean("seedService", SeedService.class);
		}
		
		
		protected void reduce(Text key, Iterable<NullWritable> values,
				Context context) throws IOException, InterruptedException {
			
			seedService.addRCSeed(key.toString());
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
		TaskData.submitFeedOutput(job);

		// 设置计算流程
		job.setMapperClass(UrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(ReadyUrlReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
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
		SeedGenerator generator = context.getBean("seedGenerator", SeedGenerator.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), generator, args);
		System.exit(res);
	}
}
