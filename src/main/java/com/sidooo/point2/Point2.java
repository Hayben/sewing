package com.sidooo.point2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;

public class Point2 implements Writable{

	private String docId;
	
	private String title;
	
	private String url;

	public Point2() {
		docId = "";
		title = "";
	}
	
	public void clear() {
		this.docId = null;
		this.title = null;
		this.url = null;
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
		this.title = title;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.docId);
		out.writeUTF(this.title);
		out.writeUTF(this.url);
 	}
	
	public static Point2 read(DataInput in) throws IOException {
		Point2 point = new Point2();
		point.readFields(in);
		return point;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		
		this.docId = in.readUTF();
		this.title = in.readUTF();
		this.url = in.readUTF();;
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
