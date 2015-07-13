package com.sidooo.point;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sidooo.ai.Keyword;


/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-1-10
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
@Document(collection = "point")
public class Point implements Writable{
	
	//Document ID
	@Id
	@Field("id")
	private String docId;
	
	@Field("title")
	private String title;
	
	@Field("url")
	private String url;
	
	@Field("links")
	private Set<Keyword> links;
	
	public Point() {
		docId = "";
		title = "";
		links = new HashSet<Keyword>();
	}
	
	public void clear() {
		this.docId = null;
		this.title = null;
		this.url = null;
		this.links.clear();
	}
	
	public String getDocId() {
		return this.docId;
	}
	
	public void setDocId(String id) {
		this.docId = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		
		if (title == null) {
			this.title = "";
		} else {
			this.title = title;
		}
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Keyword[] getLinks() {
		return links.toArray(new Keyword[links.size()]);
	}
	
	public void addLink(Keyword keyword) {
		this.links.add(keyword);
	}
	
	public void removeLinks() {
		this.links.clear();
	}
	
	public boolean existLink(String linkId) {
		for(Keyword link : links) {
			if (link.getWord().equals(linkId)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.docId);
		out.writeUTF(this.title);
		out.writeUTF(this.url);
		out.writeInt(this.links.size());
		for(Keyword link : links) {
			link.write(out);
		}
 	}
	
	public static Point read(DataInput in) throws IOException {
		Point point = new Point();
		point.readFields(in);
		return point;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		
		this.docId = in.readUTF();
		this.title = in.readUTF();
		this.url = in.readUTF();
		this.links.clear();
		int count = in.readInt();
		for(int i=0; i<count; i++) {
			Keyword link = Keyword.read(in);
			this.links.add(link);
		}
	}
	
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("Point:"+this.docId+", Keyword:");
		for(Keyword keyword : links) {
			build.append(keyword.getWord()+",");
		}
		
		return build.toString();
	}
	
	public static String md5(String content) {
		
    	MessageDigest digest;
    	try {
    		digest = MessageDigest.getInstance("MD5");
    	} catch(Exception e) {
    		return "ffffffff00000000";
    	}
    	
    	digest.update(content.getBytes());
    	byte[] md5 = digest.digest();
    	return Bytes.toHex(md5);
//    	StringBuffer hexString = new StringBuffer();
//    	for (int i = 0; i < md5.length; i++) {
//          String shaHex = Integer.toHexString(md5[i] & 0xFF);
//          if (shaHex.length() < 2) {
//              hexString.append(0);
//          }
//          hexString.append(shaHex);
//    	}
//    	return hexString.toString();
	}
	


}
