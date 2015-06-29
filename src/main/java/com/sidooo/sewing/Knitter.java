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

import com.sidooo.ai.Keyword;
import com.sidooo.point.Link;
import com.sidooo.point.Point;
import com.sidooo.senode.DatawareConfiguration;

@Service("knitter")
public class Knitter extends SewingConfigured implements Tool {

	public static class LinkMapper extends SewingMapReduce implements
			Mapper<Text, Point, Keyword, Text> {

		@Override
		public void map(Text pointId, Point point,
				OutputCollector<Keyword, Text> output, Reporter reporter)
				throws IOException {

			Keyword[] links = point.getLinks();
			for (Keyword link : links) {
				LOG.info("Keyword:" + link.getWord());
				output.collect(link, pointId);
			}
		}

	}

	public static class LinkReducer extends SewingMapReduce implements
			Reducer<Keyword, Text, Text, Link> {

		@Override
		public void reduce(Keyword keyword, Iterator<Text> pointList,
				OutputCollector<Text, Link> output, Reporter reporter)
				throws IOException {

			
			Link link = new Link();
			link.setKeyword(keyword.getWord());
			link.setType(keyword.getAttr());
			while (pointList.hasNext()) {
				Text pointId = pointList.next();
				link.addPoint(pointId.toString());
			}

			LOG.info("Keyword:"+keyword.getWord() + ", Count:" + link.getPointList().length);
			output.collect(new Text(keyword.getWord()), link);
		}

	}

	@Override
	public int run(String[] args) throws Exception {
		JobConf job = new JobConf(getConf(), Knitter.class);

		job.setJobName("Sewing Knit");

		submitPointInput(job);

		submitLinkOutput(job);

		job.setMapperClass(LinkMapper.class);
		job.setMapOutputKeyClass(Keyword.class);
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
