package com.sidooo.sewing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sidooo.crawl.Filter;
import com.sidooo.extractor.ContentDecompressor;
import com.sidooo.seed.Seed;

public class SewingMapReduce extends MapReduceBase {

	protected final Logger LOG = LoggerFactory.getLogger("Sewing");

	protected List<Seed> seeds = null;
	
	protected Filter filter = new Filter();

	protected boolean accept(String address) {
		if (!filter.accept(address)) {
			return false;
		}

		String url = address.toLowerCase();
		for (Seed seed : seeds) {
			if (url.startsWith(seed.getUrl().toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	protected void loadSeedList(LocalFileSystem lfs, Path seedFile) {

		InputStream in = null;
		try {
			in = lfs.open(seedFile);
			LOG.info("seed.json length:" + in.available());
		} catch (Exception e) {
			LOG.error("open seed.json error.", e);
			return;
		}

		Gson gson = new Gson();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			seeds = gson.fromJson(reader, new TypeToken<List<Seed>>() {
			}.getType());
			LOG.info("Seed Count: " + seeds.size());
		} catch (Exception e) {
			LOG.error("read seed.json error.", e);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {

			}
		}
	}
	
	protected void loadHanlpData(LocalFileSystem lfs, Path dataFile) throws Exception {
		
		LOG.info("HanLP Data File: " + dataFile.toString());
		ContentDecompressor decompressor = new ContentDecompressor();
		decompressor.decompressGzipArchive(dataFile.toString());
	}

	protected void checkCacheFiles(JobConf conf) {

		LocalFileSystem lfs = null;
		try {
			lfs = FileSystem.getLocal(conf);
		} catch (IOException e) {
			LOG.error("GetLocalFileSystem error.", e);
		}

		Path[] cacheFiles = null;
		try {
			cacheFiles = DistributedCache.getLocalCacheFiles(conf);
		} catch (IOException e) {
			LOG.error("List Cache Files error.", e);
		}
		if (cacheFiles != null) {
			for (Path cacheFile : cacheFiles) {
				LOG.info("Cache File: " + cacheFile.getName());
				if (cacheFile.getName().equals("seed.json")) {
					loadSeedList(lfs, cacheFile);
	//			} else if (cacheFile.getName().equals("data.tar.gz")) {
	//				try {
	//					loadHanlpData(lfs, cacheFile);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//					LOG.error("Load Hanlp Data error." , e);
	//				}
				} else {
					
				}
			}
		}
		
		Path[] cacheArchives = null;
		try {
			cacheArchives = DistributedCache.getLocalCacheArchives(conf);
		} catch (IOException e) {
			LOG.error("List Cache Arahives error.", e);
		}
		if (cacheArchives != null) {
			for( Path cacheArchive : cacheArchives) {
				String archiveName = cacheArchive.getName();
				LOG.info("Cache Archive: " + archiveName);
				if (archiveName.equals("data.tar.gz")) {
					LOG.info("data.tar.gz : " + cacheArchive.toString());
	//				try {
	//					loadHanlpData(lfs, cacheArchive);
	//					LOG.info("Load HanLP Data Succeed.");
	//					if (!lfs.exists(new Path("data/dictionary/CoreNatureDictionary.ngram.txt"))) {
	//						LOG.error("data/dictionary/CoreNatureDictionary.ngram.txt not exist");
	//					} else {
	//						LOG.info("data/dictionary/CoreNatureDictionary.ngram.txt exist");
	//					}
	//				} catch (Exception e) {
	//					LOG.error("check hanlp data error.", e);
	//				} 
				} else if (archiveName.equals("hanlp-1.2.2.jar")){
					
				} else {
					
				}
			}
		}
		
	}

	protected Seed getSeedByUrl(String url) {
		boolean inSeed = false;
		for (Seed seed : seeds) {

			if (url.contains(seed.getUrl())) {
				return seed;
			}
		}

		return null;
	}

}
