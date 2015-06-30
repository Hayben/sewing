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

import com.sidooo.point.Point;
import com.sidooo.point.PointRepository;
import com.sidooo.point.PointService;
import com.sidooo.senode.DatawareConfiguration;

@Service("deployPoint")
public class DeployPoint extends SewingConfigured implements Tool{
	
	@Autowired
	private PointService pointService;
	
	public static class DeployPointMapper extends SewingMapReduce implements
			Mapper<Text, Point, NullWritable, NullWritable> {

		private PointRepository pointRepo;

		@Override
		public void configure(JobConf conf) {
			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
					DatawareConfiguration.class);
			context.scan("com.sidooo");
			pointRepo = context.getBean("pointRepository",
					PointRepository.class);
		}

		@Override
		public void map(Text key, Point value,
				OutputCollector<NullWritable, NullWritable> output,
				Reporter reporter) throws IOException {
			pointRepo.createPoint(value);
		}

		@Override
		public void close() throws IOException {

		}

	}
	
	@Override
	public int run(String[] args) throws Exception {

		pointService.clearPoints();
		
		JobConf job = new JobConf(getConf(), DeployPoint.class);
		job.setJobName("Sewing Deploy Point");

		submitPointInput(job);
		
		submitNullOutput(job);

		job.setMapperClass(DeployPointMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setNumReduceTasks(0);
		
		JobClient.runJob(job);

		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		DeployPoint deploy = context.getBean("deployPoint", DeployPoint.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), deploy, args);
		System.exit(res);
	}
}
