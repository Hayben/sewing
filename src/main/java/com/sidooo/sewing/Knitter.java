package com.sidooo.sewing;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.sidooo.point.Link;
import com.sidooo.point.Point;
import com.sidooo.senode.DatawareConfiguration;

@Service("knitter")
public class Knitter extends SewingConfigured implements Tool {

	public static class LinkMapper extends SewingMapReduce implements
			Mapper<Text, Point, Text, Text> {

		@Override
		public void map(Text pointId, Point point,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {

			String[] links = point.getLinks();
			for (String link : links) {
				output.collect(new Text(link), pointId);
			}
		}

	}

	public static class LinkReducer extends SewingMapReduce implements
			Reducer<Text, Text, Text, Link> {

		@Override
		public void reduce(Text keyword, Iterator<Text> pointList,
				OutputCollector<Text, Link> output, Reporter reporter)
				throws IOException {

			Link link = new Link();
			link.setKeyword(keyword.toString());
			link.setType("");
			while (pointList.hasNext()) {
				Text pointId = pointList.next();
				link.addPoint(pointId.toString());
			}

			output.collect(keyword, link);
		}

	}

	@Override
	public int run(String[] args) throws Exception {
		JobConf job = new JobConf(getConf(), Knitter.class);

		job.setJobName("Sewing Knit");

		submitPointInput(job);

		submitLinkOutput(job);

		job.setMapperClass(LinkMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setReducerClass(LinkReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Link.class);

		JobClient.runJob(job);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				DatawareConfiguration.class);
		context.scan("com.sidooo");
		Knitter crawl = context.getBean("knitter", Knitter.class);

		int res = ToolRunner.run(SewingConfiguration.create(), crawl, args);
		System.exit(res);
	}
}
