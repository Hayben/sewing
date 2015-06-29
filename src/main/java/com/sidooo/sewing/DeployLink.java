package com.sidooo.sewing;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.point.Link;
import com.sidooo.point.LinkRepository;
import com.sidooo.point.PointService;
import com.sidooo.senode.DatawareConfiguration;

@Service("deployLink")
public class DeployLink extends SewingConfigured implements Tool{
	
	@Autowired
	private PointService pointService;
	
	public static class DeployLinkMapper extends SewingMapReduce implements
			Mapper<Text, Link, NullWritable, NullWritable> {

		private LinkRepository linkRepo;

		@Override
		public void configure(JobConf conf) {
			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
					DatawareConfiguration.class);
			context.scan("com.sidooo");
			linkRepo = context.getBean("linkRepository", LinkRepository.class);
		}

		@Override
		public void map(Text key, Link value,
				OutputCollector<NullWritable, NullWritable> output,
				Reporter reporter) throws IOException {
			LOG.info("Keyword:"+value.getKeyword() + ", Point Count:" + value.getPointList().length);
			linkRepo.createLink(value);
		}

		@Override
		public void close() throws IOException {
		}

	}
	
	@Override
	public int run(String[] args) throws Exception {

		pointService.clearLinks();
		
		JobConf job = new JobConf(getConf(), DeployLink.class);
		job.setJobName("Sewing Deploy Link");

		submitLinkInput(job);
		
		submitNullOutput(job);

		job.setMapperClass(DeployLinkMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(NullWritable.class);

		JobClient.runJob(job);
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		DeployLink deploy = context.getBean("deployLink", DeployLink.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), deploy, args);
		System.exit(res);
	}
}
