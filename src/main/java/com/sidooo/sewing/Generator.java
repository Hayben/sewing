package com.sidooo.sewing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.crawl.UrlStatus;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("generator")
public class Generator extends Configured implements Tool {

	public static final Logger LOG = LoggerFactory.getLogger("Generator");

	@Autowired
	private SeedService seedService;

	public static class ReadyUrlMapper extends
			Mapper<Text, IntWritable, Text, NullWritable> {

		@Override
		public void map(Text key, IntWritable value, Context context)
				throws IOException, InterruptedException {

			UrlStatus status = UrlStatus.valueOf(value.get());

			if (status == UrlStatus.READY) {
				context.write(key, NullWritable.get());
			}

		}
	}

	public static class ReadyUrlReducer extends
			Reducer<Text, NullWritable, Text, NullWritable> {

		private int count = 0;
		
		protected void reduce(Text key, Iterable<NullWritable> values,
				Context context) throws IOException, InterruptedException {
			if (count <= 50000) {
				context.write(key, NullWritable.get());
				count ++;
				context.getCounter("Sewing", "FEED").increment(1);
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
		TaskData.submitUrldbInput(job);

		// 设置输出
		TaskData.submitFeedOutput(job);

		// 设置计算流程
		job.setMapperClass(ReadyUrlMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setReducerClass(ReadyUrlReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		job.setNumReduceTasks(35);

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterGroup group = job.getCounters().getGroup("Sewing");
			long urlCount = group.findCounter("FEED").getValue();
			System.out.println("Feed Count: " + urlCount);
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
