package com.sidooo.wheart;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import com.sidooo.ai.IDKeyword;
import com.sidooo.ai.Recognition;
import com.sidooo.entity.Entity;
import com.sidooo.extractor.ContentExtractor;
import com.sidooo.fetcher.Fetcher;
import com.sidooo.item.Item;
import com.sidooo.item.ItemRepository;
import com.sidooo.point.Link;
import com.sidooo.point.Point;
import com.sidooo.point.PointRepository;
import com.sidooo.queue.QueueRepository;
import com.sidooo.seed.Account;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedRepository;
import com.sidooo.seed.SeedUrl;
import com.sidooo.snapshot.SnapFile;
import com.sidooo.snapshot.SnapFileInputStream;
import com.sidooo.snapshot.SnapFileOutputStream;
import com.sidooo.snapshot.Snapshot;
import com.sidooo.snapshot.SnapshotRepository;

public class Processor extends Thread{

	private SeedRepository seedRepo;
	
	private PointRepository pointRepo;
	
	private ItemRepository itemRepo;
	
	private SnapshotRepository snapRepo;
	
	private QueueRepository queue;

	private Filter filter = new Filter();
	
	private final Logger logger = Logger.getRootLogger();

	public Processor(PointRepository pointRepo, ItemRepository itemRepo, 
			SnapshotRepository snapRepo, 
			SeedRepository seedRepo, 
			QueueRepository queue) {
		this.pointRepo = pointRepo;
		this.itemRepo = itemRepo;
		this.snapRepo = snapRepo;
		this.seedRepo = seedRepo;
		this.queue = queue;
	}

	public void start() {
		
		while(true) {
			
			SeedUrl request = queue.receiveSeedUrl();
			
			Seed seed = seedRepo.getSeed(request.seedId);
			if (seed == null) {
				continue;
			}
			
			URL url;
			try {
				url = new URL(request.url);
			} catch (Exception e) {
				logger.error(e);
				continue;
			}

			if ("smb".equals(url.getProtocol())) {
				Account account = seed.getAccount();

				NtlmPasswordAuthentication auth = 
						new NtlmPasswordAuthentication(
								url.getHost(), account.username, account.password);
				SmbFile dir = new SmbFile(url, auth);
				try {
					if (dir.isDirectory()) {
						SmbFile[] subFiles = dir.listFiles("*");
						for(SmbFile subFile : subFiles) {
							if (!filter.accept(subFile.getPath())) {
								queue.sendSeedUrl(request.seedId, subFile.getPath());
							}
						}
					}
				} catch(Exception e) {
					logger.error(e);
				}
				continue;
			}
			
			if (snapRepo.existSnapshot(url)) {
				continue;
			}
			
			SnapFile snapFile;
			try {
				snapFile = snapRepo.createSnapFile(url);
			} catch (Exception e) {
				logger.error(e);
				continue;
			}
			

			OutputStream out;
			try {
				out = new SnapFileOutputStream(snapFile);
			} catch (Exception e) {
				logger.error(e);
				continue;
			} 
			
			Fetcher fetcher = Fetcher.getInstance(request.url, out);
			try {
				fetcher.fetch();
			} catch (Exception e) {
				logger.error(e);
				snapFile.delete();
				continue;
			} finally {
				try {
					out.close();
				} catch(Exception e) {
					logger.error(e);
				}
			}
			
			try {
				snapRepo.createSnapshot(url, snapFile);
			} catch(Exception e) {
				logger.error(e);
			}
			
			InputStream in;
			try {
				in = new SnapFileInputStream(snapFile);
			} catch(Exception e) {
				logger.error(e);
				continue;
			}

			ContentExtractor extractor = 
					ContentExtractor.getInstance(snapFile.getPath(), in);
			if (extractor == null) {
				continue;
			}
			
			do {
				extractor.extract();
				String[] links = extractor.getLinks();
				for(String link : links) {
					if (!filter.accept(link)) {
						queue.sendSeedUrl(request.seedId, link);
					}
				}
				
				String title = extractor.getTitle();
				String[] contents = extractor.getContents();
				for(String content : contents) {
					
					Item item = new Item();
					item.seed = seed.getId();
					item.snapUrl = snapFile.getPath();
					item.originUrl = url.toString();
					item.content = content;
					item.title = title;
					String itemId = itemRepo.saveItem(item);
					
					Recognition recog = new Recognition();
					IDKeyword[] keywords = recog.search(content);
					for(IDKeyword keyword : keywords) {
						Entity entity = new Entity();
						entity.keyword = keyword.word;
						entity.attribute = keyword.attr;
						entity.item = itemId;
						entity.brief = item.title;
						joinNetwork(entity);
					}
				}
			}while(!extractor.finished());

		}
	}



	
	private void joinNetwork(Entity entity) {
		
		Link link = pointRepo.getLink(entity.keyword);
		if (link == null) {
			link = new Link();
			link.setKeyword(entity.keyword);
			link.addPoint(entity.item);
			link.setType(entity.attribute);
		} else {
			if (!link.existPoint(entity.item)) {
				link.addPoint(entity.item);
				pointRepo.updateLink(link);
			}
		}
		
		Point point = pointRepo.getPoint(entity.item);
		if (point == null) {			
			point = new Point();
			point.setDocumentId(entity.item);
			point.addLink(entity.keyword);
			point.setTitle(entity.brief);
			point.addLink(entity.keyword);
			pointRepo.createPoint(point);
		} else {
			if (!point.existLink(entity.keyword)) {
				point.addLink(entity.keyword);
				pointRepo.updatePoint(point);
			}
			
		}
		
	}

}
