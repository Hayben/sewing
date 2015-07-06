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
import java.util.List;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sidooo.seed.Seed;

public class CacheSaver {

	public static final Logger LOG = LoggerFactory.getLogger("CacheSaver");

	private static long getFileSize(FileSystem hdfs, Path file)
			throws IOException {
		ContentSummary summary = hdfs.getContentSummary(file);
		return summary.getLength();
	}

	private static int generateSeedList(List<Seed> seeds, File seedFile)
			throws IOException {

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
				seedFile));
		Gson gson = new Gson();
		gson.toJson(seeds, out);

		out.close();
		return seeds.size();
	}

	private static void uploadSeedList(FileSystem hdfs, File seedFile,
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
	
	private static void uploadFile(FileSystem hdfs, File localFile, Path remoteFile)
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

	public static void submitSeedCache(Job job, List<Seed> seeds)
			throws Exception {
		FileSystem hdfs = FileSystem.get(job.getConfiguration());

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
		DistributedCache.addCacheFile(uriSeedCache, job.getConfiguration());
		LOG.info("Add Cache File: " + uriSeedCache.toString());
	}

	// 提交实体识别数据库
	public static void submitNlpCache(Job job) throws Exception {

		FileSystem hdfs = FileSystem.get(job.getConfiguration());

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
		if (getFileSize(hdfs, remoteNlpJar) != localNlpJar.length()) {
			LOG.error("Upload File Size Error," + " LocalFileSize:"
					+ localNlpJar.length() + " RemoteFileSize:" + remoteNlpJar);
			throw new Exception("Upload Nlp Jar Length mismatch Local File.");
		}
		DistributedCache.addArchiveToClassPath(remoteNlpJar, job.getConfiguration());
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

		DistributedCache.addCacheArchive(remoteNlpFile.toUri(), job.getConfiguration());
		LOG.info("Add Cache File: " + remoteNlpFile.toString());
	}

}
