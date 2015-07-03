package com.sidooo.sewing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sidooo.seed.Seed;

public class CacheLoader {

	private static Path getFile(Configuration conf, String fileName)
			throws IOException {
		Path[] cacheFiles = DistributedCache.getLocalCacheFiles(conf);

		if (cacheFiles != null) {
			for (Path cacheFile : cacheFiles) {
				if (cacheFile.getName().equals(fileName)) {
					return cacheFile;
				}
			}
		}

		return null;

	}

	private static Path getArchive(Configuration conf, String archiveName)
			throws IOException {

		Path[] cacheArchives = DistributedCache.getLocalCacheArchives(conf);
		
		if (cacheArchives == null) {
			return null;
		}
		
		for (Path cacheArchive : cacheArchives) {
			if (cacheArchive.getName().equals(archiveName)) {
				return cacheArchive;
			}
		}
		
		return null;
	}

	public static List<Seed> loadSeedList(Configuration conf) throws IOException {

		Path seedFile = getFile(conf, "seed.json");
		if (seedFile == null) {
			throw new IOException("seed.json not found.");
		}

		LocalFileSystem lfs = FileSystem.getLocal(conf);

		InputStream in = lfs.open(seedFile);

		Gson gson = new Gson();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			return gson.fromJson(reader, new TypeToken<List<Seed>>() {
			}.getType());
		} finally {
			try {
				reader.close();
			} catch (Exception e) {

			}
		}
	}
	
	public static boolean existHanlpJar(Configuration conf) throws IOException {
		Path jarFile = getArchive(conf, "hanlp-1.2.2.jar");
		return jarFile != null;
	}
	
	public static boolean exiistHanlpData(Configuration conf) throws IOException {
		Path dataFile = getArchive(conf, "data.tar.gz");
		return dataFile != null;
		
		// LOG.info("Load HanLP Data Succeed.");
		// if (!lfs.exists(new
		// Path("data/dictionary/CoreNatureDictionary.ngram.txt")))
		// {
		// LOG.error("data/dictionary/CoreNatureDictionary.ngram.txt not exist");
		// } else {
		// LOG.info("data/dictionary/CoreNatureDictionary.ngram.txt exist");
		// }
		// } catch (Exception e) {
		// LOG.error("check hanlp data error.", e);
		// }
	}
	

}
