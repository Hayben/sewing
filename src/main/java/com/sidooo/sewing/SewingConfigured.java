package com.sidooo.sewing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.MapFile.Writer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.FetchStatus;
import com.sidooo.seed.Seed;

public class SewingConfigured extends Configured {

	public static final Logger LOG = LoggerFactory.getLogger("Sewing");

	protected long getFileSize(Path file) throws IOException {
		FileSystem hdfs = FileSystem.get(getConf());
		return getFileSize(hdfs, file);
	}

	private long getFileSize(FileSystem hdfs, Path file) throws IOException {
		ContentSummary summary = hdfs.getContentSummary(file);
		return summary.getLength();
	}

	public int generateSeedList(List<Seed> seeds, File seedFile)
			throws IOException {

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
				seedFile));
		Gson gson = new Gson();
		gson.toJson(seeds, out);

		out.close();
		return seeds.size();
	}

	private void uploadSeedList(FileSystem hdfs, File seedFile,
			Path remoteSeedFile) throws Exception {

		FSDataOutputStream out = hdfs.create(remoteSeedFile);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out, "UTF-8"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(seedFile), "UTF-8"));
			int size = 0;
			char[] buffer = new char[16 * 1024];
			while ((size = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, size);
			}
			writer.close();
			reader.close();
		} finally {
			out.close();
		}
	}

	private void uploadFile(FileSystem hdfs, File localFile, Path remoteFile)
			throws Exception {
		
		FSDataOutputStream out = hdfs.create(remoteFile);
		try {
			BufferedOutputStream writer = new BufferedOutputStream(out);
			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(localFile));
			int size = 0;
			byte[] buffer  = new byte[16*1024];
			while((size = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, size);
			}
			writer.close();
			reader.close();
		} finally {
			out.close();
		}
	}

	// 提交种子列表到分布式缓存中
	protected void submitSeedCache(JobConf job, List<Seed> seeds)
			throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		LOG.info("Generate Seed List ...");
		File localSeedFile = new File("seed.json");
		LOG.info("Seed File: " + localSeedFile.getPath());
		int seedCount = generateSeedList(seeds, localSeedFile);
		LOG.info("Seed File Size:" + localSeedFile.length());
		LOG.info("Enabled Seed Count:" + seedCount);

		// 将爬虫种子列表上传HDFS
		LOG.info("Upload Seed List File to HDFS");
		Path remoteSeedFile = new Path("/sewing/seed.json");
		if (hdfs.exists(remoteSeedFile)) {
			hdfs.delete(remoteSeedFile);
		}
		uploadSeedList(hdfs, localSeedFile, remoteSeedFile);
		long uploadSize = getFileSize(hdfs, remoteSeedFile);
		if (uploadSize != localSeedFile.length()) {
			LOG.error("Upload File Size Error," + " LocalFileSize:"
					+ localSeedFile.length() + " RemoteFileSize:" + uploadSize);
			throw new Exception("Upload Seeds File Length mismatch Local File.");
		}

		// 将种子列表加入分布式缓存中，分发给所有slavers
		URI uriSeedCache = new URI(remoteSeedFile.toUri().toString()
				+ "#seed.json");
		DistributedCache.addCacheFile(uriSeedCache, job);
		LOG.info("Add Cache File: " + uriSeedCache.toString());
	}
	
	// 提交实体识别数据库
	protected void submitNlpCache(JobConf job) throws Exception {
		
		FileSystem hdfs = FileSystem.get(job);
		
		File localNlpJar = new File("hanlp-1.2.2.jar");
		if (!localNlpJar.exists()) {
			throw new Exception("hanlp.jar not found");
		}
		Path remoteNlpJar = new Path("/sewing/hanlp-1.2.2.jar");
		if (hdfs.exists(remoteNlpJar)) {
			if (getFileSize(hdfs, remoteNlpJar) == localNlpJar.length()) {
				LOG.info("Nlp Jar File is latest.");
			} else {
				hdfs.delete(remoteNlpJar);
			}
		} 
		
		uploadFile(hdfs, localNlpJar, remoteNlpJar);
		if (getFileSize(hdfs,remoteNlpJar) != localNlpJar.length()) {
			LOG.error("Upload File Size Error," + " LocalFileSize:"
					+ localNlpJar.length() + " RemoteFileSize:" + remoteNlpJar);
			throw new Exception("Upload Nlp Jar Length mismatch Local File.");
		}
		DistributedCache.addArchiveToClassPath(remoteNlpJar, job);
		LOG.info("Add Cache File: " + remoteNlpJar.toString());
		
		LOG.info("Generate Nlp Data ...");
		File localNlpFile = new File("data.tar.gz");
		if (!localNlpFile.exists()) {
			throw new Exception("data.tar.gz not found.");
		}
		LOG.info("Nlp Data File: " + localNlpFile.getPath());
		LOG.info("Nlp Data File Size: " + localNlpFile.length());
		
		Path remoteNlpFile = new Path("/sewing/data.tar.gz");
		if (hdfs.exists(remoteNlpFile)) {
			long fileSize = getFileSize(hdfs, remoteNlpFile);
			if (fileSize == localNlpFile.length()) {
				LOG.info("Nlp Data File is latest.");
			} else {
				hdfs.delete(remoteNlpFile);
			}
		}
		
		uploadFile(hdfs, localNlpFile, remoteNlpFile);
		long uploadSize = getFileSize(hdfs, remoteNlpFile);
		if (uploadSize != localNlpFile.length()) {
			LOG.error("Upload File Size Error," + " LocalFileSize:"
					+ localNlpFile.length() + " RemoteFileSize:" + uploadSize);
			throw new Exception("Upload Nlp File Length mismatch Local File.");
		}
		
		URI uriNlpFile = new URI(remoteNlpFile.toUri().toString() + "#data.tar.gz");
		DistributedCache.addCacheArchive(remoteNlpFile.toUri(), job);
		LOG.info("Add Cache File: " + remoteNlpFile.toString());
	}
	
	protected void submitCountInput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path countFile = new Path("/sewing/count.sequence");
		if(hdfs.exists(countFile)) {
			hdfs.delete(countFile);
		}
		
		FileInputFormat.addInputPath(job, countFile);
		job.setInputFormat(SequenceFileInputFormat.class);
		
		LOG.info("Submit Count Input File:" + countFile.toString());
	}
	
	protected void submitCountOutput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path countFile = new Path("/sewing/count.sequence");
		if (!hdfs.exists(countFile)) {
			throw new Exception("Count File not found.");
		}

		FileOutputFormat.setOutputPath(job, countFile);
		job.setOutputFormat(SequenceFileOutputFormat.class);
		
		SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.RECORD);
	}
	

	protected void submitUrlInput(JobConf job) throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path urlFile = new Path("/sewing/urls.txt");
		if (!hdfs.exists(urlFile)) {
			throw new Exception("urls.txt not found.");
		}

		FileInputFormat.setInputPaths(job, urlFile);
		job.setInputFormat(TextInputFormat.class);
	}

	protected Path submitUrlOutput(JobConf job) throws IOException {
		FileSystem hdfs = FileSystem.get(job);

		Path urlFile = new Path("/sewing/urls.txt");
		if (hdfs.exists(urlFile)) {
			hdfs.delete(urlFile);
		}

		FileOutputFormat.setOutputPath(job, urlFile);
		job.setOutputFormat(TextOutputFormat.class);
		
		return urlFile;
	}

	// 提交所有爬虫数据到输入中
	protected int submitCrawlInput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path crawlDir = new Path("/sewing/crawl");
		if (!hdfs.exists(crawlDir)) {
			hdfs.mkdirs(crawlDir);
		}
		if (!hdfs.isDirectory(crawlDir)) {
			throw new Exception("HDFS /sewing/crawl is not directory.");
		}
		int count = 0;
		FileStatus[] status = hdfs.listStatus(crawlDir);
		for(int i=0; i< status.length; i++) {
			Path file = status[i].getPath();
			if (file.getName().endsWith(".sequence")) {
				LOG.info("Submit Crawl Input File: " + file.getName());
				FileInputFormat.addInputPath(job, file);
				count++;
			}
		}

		job.setInputFormat(SequenceFileInputFormat.class);
		return count;
	}

	protected void submitSeedInput(JobConf job, List<Seed> seeds)
			throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path seedFile = new Path("/sewing/seed.sequence");
		if(hdfs.exists(seedFile)) {
			hdfs.delete(seedFile);
		}

		SequenceFile.Writer writer = null;
		writer = SequenceFile.createWriter(hdfs, job, seedFile, Text.class,
				FetchContent.class,
				CompressionType.RECORD, new GzipCodec());

		for (Seed seed : seeds) {
			FetchContent content = new FetchContent();
			content.setStatus(0);
			writer.append(new Text(seed.getUrl()), content);
		}
		writer.close();
		
		FileInputFormat.addInputPath(job, seedFile);
		job.setInputFormat(SequenceFileInputFormat.class);
		
		LOG.info("Submit Seed Input File:" + seedFile.toString());
	}

	protected void submitCrawlOutput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path crawlDir = new Path("/sewing/crawl");
		if (!hdfs.exists(crawlDir)) {
			hdfs.mkdirs(crawlDir);
		}

		// 根据日期创建工作目录
		Date date = new Date();
		String taskId = (new SimpleDateFormat("yyyyMMddHHmm")).format(date);
		Path taskPath = new Path("/sewing/crawl/" + taskId+".sequence");
		LOG.info("Crawl Task Name: " + taskId);
		FileOutputFormat.setOutputPath(job, taskPath);
		job.setOutputFormat(SequenceFileOutputFormat.class);
		
		SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.RECORD);
	}

	protected void submitItemInput(JobConf job) throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path itemFile = new Path("/sewing/item");
		if (!hdfs.exists(itemFile)) {
			throw new Exception("Item file not found.");
		}

		job.setInputFormat(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, itemFile);
	}

	protected void submitItemOutput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path itemFile = new Path("/sewing/item");
		if (hdfs.exists(itemFile)) {
			hdfs.delete(itemFile);
		}

		job.setOutputFormat(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, itemFile);
		SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.RECORD);
	}

	protected void submitPointOutput(JobConf job) throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path pointFile = new Path("/sewing/point");
		if (hdfs.exists(pointFile)) {
			hdfs.delete(pointFile);
		}

		job.setOutputFormat(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, pointFile);
		SequenceFileOutputFormat.setCompressOutput(job, false);
//		SequenceFileOutputFormat.setCompressOutput(job, true);
//        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
//        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.RECORD);
	}

	protected void submitPointInput(JobConf job) throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path pointFile = new Path("/sewing/point");
		if (!hdfs.exists(pointFile)) {
			throw new Exception("Point file not exist.");
		}

		job.setInputFormat(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, pointFile);
	}

	protected void submitLinkOutput(JobConf job) throws Exception {

		FileSystem hdfs = FileSystem.get(job);

		Path linkFile = new Path("/sewing/link.sequence");
		if (hdfs.exists(linkFile)) {
			hdfs.delete(linkFile);
		}

		job.setOutputFormat(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, linkFile);
		SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.RECORD);
	}

	protected void submitLinkInput(JobConf job) throws Exception {
		FileSystem hdfs = FileSystem.get(job);

		Path linkFile = new Path("/sewing/link.sequence");
		if (!hdfs.exists(linkFile)) {
			throw new Exception("Link file not exist.");
		}

		job.setInputFormat(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, linkFile);
	}
	
	protected void submitNullOutput(JobConf job) throws Exception {
		
		job.setOutputFormat(NullOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(NullWritable.class);
		job.setNumReduceTasks(0);
		
	}


}
