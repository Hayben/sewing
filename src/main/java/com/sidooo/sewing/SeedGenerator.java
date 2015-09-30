package com.sidooo.sewing;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.HttpFetcher;
import com.sidooo.crawl.UrlStatus;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("seedGenerator")
public class SeedGenerator extends Configured implements Tool{

	public static final Logger LOG = LoggerFactory.getLogger("SeedGenerator");
	
	@Autowired
	private SeedService seedService;

	public static class UrlMapper extends
			Mapper<Text, IntWritable, Text, NullWritable> {

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
		public void map(Text key, IntWritable value, Context context)
				throws IOException, InterruptedException {

			String url = key.toString();
			
			UrlStatus status = UrlStatus.valueOf(value.get());
			
			if (status == UrlStatus.FILTERED 
					&& SeedService.getSeedByUrl(url, seeds) == null
					&& url.indexOf("gov.cn") > 0) {
				
				URI uri;
				try {
					uri = new URI(url);
				} catch (URISyntaxException e) {
					return;
				}
				String seedUrl = uri.getScheme() + "://" + uri.getHost() + "/";
				context.write(new Text(seedUrl), NullWritable.get());
			}

		}
	}

	public static class UnseedUrlReducer extends
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
			
			
			try {
				 Document doc = Jsoup.connect(key.toString()).timeout(5000).get(); 
				 String title = doc.title();
				 
				 Seed seed = new Seed();
				 seed.setUrl(key.toString());
				 seed.setName(title);
				 seed.setEnabled(true);
				 seed.setLevel("C");
				 seed.setReliability("GOV");
				 seed.setType("web");
				 seed.setDivision(0);
				
				 seedService.createSeed(seed);
				 context.getCounter("Sewing", "SEED_NEW").increment(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	@Override
	public int run(String[] arg0) throws Exception {

		// LOG.info("ZooKeeper:" + getConf().get("hbase.zookeeper.quorum"));
		
		Job job = new Job(getConf());
		job.setJobName("Sewing Seed Generator");
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
		TaskData.submitUrldbInput(job);

		// 设置输出
		TaskData.submitNullOutput(job);

		// 设置计算流程
		job.setMapperClass(UrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setReducerClass(UnseedUrlReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		job.setNumReduceTasks(5);

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			long urlCount = group.findCounter("SEED_NEW").getValue();
			System.out.println("New Seed: " + urlCount);
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
