package com.sidooo.snapshot;

import java.io.BufferedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository("snapshotRepository")
public class SnapshotRepository {

	@Autowired
	private NtlmPasswordAuthentication sambaAuth;

	@Autowired
	private MongoTemplate mongo;

	@Value("${samba.host}")
	private String sambaHost;

	@Value("${samba.snapshot}")
	private String snapshotDir;

	public SmbFile[] dir(String url) {
		try {
			SmbFile dir = new SmbFile(url, sambaAuth);
			if (dir.isDirectory()) {
				// return dir.listFiles(new SmbFileFilter() {
				// public boolean accept(SmbFile f) {
				// String ext = FilenameUtils.getExtension(f.getPath());
				// return !SNAPSHOT_EXT.equalsIgnoreCase(ext);
				// }
				// });
				//
				return dir.listFiles("*");
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public SnapFile createSnapFile(URL url) throws Exception {
		String path = "smb://" + sambaHost + snapshotDir + url.getPath();
		
		SnapFile snapFile = new SnapFile(path, sambaAuth);
		String dir = snapFile.getParent();
		SnapFile snapDir = new SnapFile(dir, sambaAuth);
		if (!snapDir.exists()) {
			snapDir.mkdirs();
		}
		
		return snapFile;
	}

	public void createSnapshot(URL url, SnapFile snapFile) throws Exception {

		Snapshot snap = new Snapshot();
		snap.originUrl = url.toString().toLowerCase();
		snap.snapUrl = snapFile.getPath();
		snap.size = snapFile.length();
		snap.date = new Date();
		mongo.save(snap);
	}

	public boolean existSnapshot(URL url) {
		Snapshot snap = mongo.findById(url.toString().toLowerCase(), Snapshot.class);
		if (snap == null) {
			return false;
		}
		
		try {
			SnapFile snapFile = new SnapFile(snap.snapUrl, sambaAuth);
			if (!snapFile.exists()) {
				return false;
			}
			
			if (snapFile.length() != snap.size) {
				snapFile.delete();
				return false;
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void deleteSnapShot(URL url) {
		
		Snapshot snap = mongo.findById(url.toString().toLowerCase(), Snapshot.class);
		if (snap != null) {
			mongo.remove(snap);
		}
		
		String path = "smb://" + sambaHost + snapshotDir + url.getPath();
		try {
			SnapFile snapFile = new SnapFile(path, sambaAuth);
			snapFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private String md5(String content) {
	// try {
	// MessageDigest digest = MessageDigest.getInstance("MD5");
	// digest.update(content.getBytes());
	// byte[] md5 = digest.digest();
	// StringBuffer hexString = new StringBuffer();
	// for (int i = 0; i < md5.length; i++) {
	// String shaHex = Integer.toHexString(md5[i] & 0xFF);
	// if (shaHex.length() < 2) {
	// hexString.append(0);
	// }
	// hexString.append(shaHex);
	// }
	// return hexString.toString();
	// } catch (Exception e) {
	// return "FFFFFFFFFFFFFFFF";
	// }
	// }
	//
	// public void saveUrl(String url, long size) {
	//
	// UrlRecord record = new UrlRecord();
	// record.hash = md5(url.toLowerCase());
	// record.url = url;
	// record.size = size;
	// record.date = new Date();
	//
	// Query query = new Query();
	// Criteria criteria = Criteria.where("id").is(record.hash);
	// query.addCriteria(criteria);
	//
	// Update update = new Update();
	// update.set("url", record.url);
	// update.set("size", record.size);
	//
	// mongoTemplate.upsert(query, update, UrlRecord.class);
	// }
	//
	// public long existUrl(String url) {
	//
	// String hash = md5(url.toLowerCase());
	//
	// UrlRecord record = mongoTemplate.findById(hash, UrlRecord.class);
	// if (record == null) {
	// return 0;
	// } else {
	// return record.size;
	// }
	//
	// }
	//
	// public void clear() {
	// mongoTemplate.dropCollection(UrlRecord.class);
	// }

}
