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

import com.sidooo.point.Item;
import com.sidooo.point.ItemRepository;
import com.sidooo.point.PointService;
import com.sidooo.senode.DatawareConfiguration;

@Service("deployItem")
public class DeployItem extends SewingConfigured implements Tool{

	@Autowired
	private PointService pointService;
	
	public static class DeployItemMapper extends SewingMapReduce implements
			Mapper<Text, Item, NullWritable, NullWritable> {
		
		private ItemRepository  itemRepo;
		
		@Override
		public void configure(JobConf conf) {
			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
					DatawareConfiguration.class);
			context.scan("com.sidooo");
			itemRepo = context.getBean("itemRepository", ItemRepository.class);
		}
		
		@Override
		public void map(Text key, Item value,
				OutputCollector<NullWritable, NullWritable> output, Reporter reporter)
				throws IOException {
			
			itemRepo.saveItem(value);
		}
	}

	
	@Override
	public int run(String[] args) throws Exception {

		pointService.clearItems();
		JobConf job = new JobConf(getConf(), DeployItem.class);
		job.setJobName("Sewing Deploy Item");

		submitItemInput(job);
		
		submitNullOutput(job);

		job.setMapperClass(DeployItemMapper.class);
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
		DeployItem deploy = context.getBean("deployItem", DeployItem.class);

		// conf.set("hbase.zookeeper.quorum",
		// "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		int res = ToolRunner.run(SewingConfiguration.create(), deploy, args);
		System.exit(res);
	}
}
