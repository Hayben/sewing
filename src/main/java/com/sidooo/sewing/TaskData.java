package com.sidooo.sewing;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sidooo.crawl.FetchContent;
import com.sidooo.seed.Seed;

public class TaskData {

	public static final Logger LOG = LoggerFactory.getLogger("TaskData");

	public static void submitCountInput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path countFile = new Path("/sewing/count.sequence");
		if (!hdfs.exists(countFile)) {
			throw new Exception("Count File not found.");
		}

		FileInputFormat.addInputPath(job, countFile);
		job.setInputFormatClass(SequenceFileInputFormat.class);

		LOG.info("Submit Count Input File:" + countFile.toString());
	}

	public static void submitCountOutput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path countFile = new Path("/sewing/count.sequence");
		if (hdfs.exists(countFile)) {
			hdfs.delete(countFile);
		}

		FileOutputFormat.setOutputPath(job, countFile);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(job,
				CompressionType.RECORD);
	}

	public static void submitUrlInput(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path urlFile = new Path("/sewing/urls.txt");
		if (!hdfs.exists(urlFile)) {
			throw new Exception("urls.txt not found.");
		}

		FileInputFormat.setInputPaths(job, urlFile);
		job.setInputFormatClass(TextInputFormat.class);
	}

	public static Path submitUrlOutput(Job job) throws IOException {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path urlFile = new Path("/sewing/urls.txt");
		if (hdfs.exists(urlFile)) {
			hdfs.delete(urlFile);
		}

		FileOutputFormat.setOutputPath(job, urlFile);
		job.setOutputFormatClass(TextOutputFormat.class);

		return urlFile;
	}

	// 提交所有爬虫数据到输入中
	public static int submitCrawlInput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path crawlDir = new Path("/sewing/crawl");
		if (!hdfs.exists(crawlDir)) {
			hdfs.mkdirs(crawlDir);
		}
		if (!hdfs.isDirectory(crawlDir)) {
			throw new Exception("HDFS /sewing/crawl is not directory.");
		}
		int count = 0;
		FileStatus[] status = hdfs.listStatus(crawlDir);
		for (int i = 0; i < status.length; i++) {
			Path file = status[i].getPath();
			if (file.getName().endsWith(".sequence")) {
				
				if (hdfs.exists(new Path(file.toString() + "/_SUCCESS"))) {
					LOG.info("Submit Crawl Input File: " + file.getName());
					FileInputFormat.addInputPath(job, file);
					count++;
				}
			}
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		return count;
	}
	
	public static int SubmitThreeCrawlInput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path crawlDir = new Path("/sewing/crawl");
		if (!hdfs.exists(crawlDir)) {
			hdfs.mkdirs(crawlDir);
		}
		if (!hdfs.isDirectory(crawlDir)) {
			throw new Exception("HDFS /sewing/crawl is not directory.");
		}
		int count = 0;
		FileStatus[] status = hdfs.listStatus(crawlDir);
		for (int i = 0; i < 2; i++) {
			Path file = status[i].getPath();
			if (file.getName().endsWith(".sequence")) {
				
				if (hdfs.exists(new Path(file.toString() + "/_SUCCESS"))) {
					LOG.info("Submit Crawl Input File: " + file.getName());
					FileInputFormat.addInputPath(job, file);
					count++;
				}
			}
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		return count;	
	}

	public static void submitSeedInput(Job job, List<Seed> seeds)
			throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path seedFile = new Path("/sewing/seed.sequence");
		if (hdfs.exists(seedFile)) {
			hdfs.delete(seedFile);
		}

		SequenceFile.Writer writer = null;
		writer = SequenceFile.createWriter(hdfs, job.getConfiguration(),
				seedFile, Text.class, FetchContent.class,
				CompressionType.RECORD, new GzipCodec());

		for (Seed seed : seeds) {
			FetchContent content = new FetchContent();
			content.setStatus(0);
			writer.append(new Text(seed.getUrl()), content);
		}
		writer.close();

		FileInputFormat.addInputPath(job, seedFile);
		job.setInputFormatClass(SequenceFileInputFormat.class);

		LOG.info("Submit Seed Input File:" + seedFile.toString());
	}

	public static void submitCrawlOutput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path crawlDir = new Path("/sewing/crawl");
		if (!hdfs.exists(crawlDir)) {
			hdfs.mkdirs(crawlDir);
		}

		// 根据日期创建工作目录
		Date date = new Date();
		String taskId = (new SimpleDateFormat("yyyyMMddHHmm")).format(date);
		Path taskPath = new Path("/sewing/crawl/" + taskId + ".sequence");
		LOG.info("Crawl Task Name: " + taskId);
		FileOutputFormat.setOutputPath(job, taskPath);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(job,
				CompressionType.RECORD);
	}

	public static void submitItemInput(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path itemFile = new Path("/sewing/item");
		if (!hdfs.exists(itemFile)) {
			throw new Exception("Item file not found.");
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, itemFile);
	}

	public static void submitItemOutput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path itemFile = new Path("/sewing/item");
		if (hdfs.exists(itemFile)) {
			hdfs.delete(itemFile);
		}

		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, itemFile);
		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(job,
				CompressionType.RECORD);
	}

	public static void submitPointOutput(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path pointFile = new Path("/sewing/point");
		if (hdfs.exists(pointFile)) {
			hdfs.delete(pointFile);
		}

		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, pointFile);
		SequenceFileOutputFormat.setCompressOutput(job, false);
		// SequenceFileOutputFormat.setCompressOutput(job, true);
		// SequenceFileOutputFormat.setOutputCompressorClass(job,
		// GzipCodec.class);
		// SequenceFileOutputFormat.setOutputCompressionType(job,
		// CompressionType.RECORD);
	}

	public static void submitPointInput(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path pointFile = new Path("/sewing/point");
		if (!hdfs.exists(pointFile)) {
			throw new Exception("Point file not exist.");
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, pointFile);
	}

	public static void submitLinkOutput(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path linkFile = new Path("/sewing/link.sequence");
		if (hdfs.exists(linkFile)) {
			hdfs.delete(linkFile);
		}

		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, linkFile);
		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(job,
				CompressionType.RECORD);
	}

	public static void submitLinkInput(Job job) throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

		Path linkFile = new Path("/sewing/link.sequence");
		if (!hdfs.exists(linkFile)) {
			throw new Exception("Link file not exist.");
		}

		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, linkFile);
	}

	public static void submitNullOutput(Job job) throws Exception {

		job.setOutputFormatClass(NullOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(NullWritable.class);

	}
}
