package com.sidooo.point;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.hadoop.io.Writable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "item")
public class Item implements Writable{
	
	@Id
	public String id;

	@Field("title")
	private String title;
	
	@Field("content")
	private String content;

	@Field("origin")
	private String originUrl;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(id);
		out.writeUTF(title);
		out.writeUTF(content);
		out.writeUTF(originUrl);
	}
	
	public static Item read(DataInput in) throws IOException {
		Item item = new Item();
		item.readFields(in);
		return item;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readUTF();
		this.title = in.readUTF();
		this.content = in.readUTF();
		this.originUrl = in.readUTF();
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setContent(String content) {
		this.content = content;
		this.id = md5(content);
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setUrl(String url) {
		this.originUrl = url;
	}
	
	public String getUrl() {
		return this.originUrl;
	}
	
	private String md5(String content) {
		
    	MessageDigest digest;
    	try {
    		digest = MessageDigest.getInstance("MD5");
    	} catch(Exception e) {
    		return "ffffffff00000000";
    	}
    	
    	digest.update(content.getBytes());
    	byte[] md5 = digest.digest();
    	StringBuffer hexString = new StringBuffer();
    	for (int i = 0; i < md5.length; i++) {
          String shaHex = Integer.toHexString(md5[i] & 0xFF);
          if (shaHex.length() < 2) {
              hexString.append(0);
          }
          hexString.append(shaHex);
    	}
    	return hexString.toString();
	}
	

	
//	public void log(String message) {
//		logs.add(message);
//	}
//	
//	public List<String> getLogs() {
//		return logs;
//	}
	
//	public String getProcotol() {
//		try {
//			URL url = new URL(this.source);
//			return url.getProtocol();
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
//	public JSONObject toJson() throws Exception{
//        JSONObject node = new JSONObject();
//        node.put("id", this.id);
//        node.put("brief", this.brief);
//        node.put("format", this.format);
//        node.put("origin", this.origin);
//        node.put("content", this.content);
//        node.put("ontology", this.ontology);
//        node.put("seed", this.seed);
//        node.put("source", this.source);
//        node.put("comment", this.comment);
//        
//        JSONArray array = new JSONArray();
//        for(String log : logs) {
//        	array.put(log);
//        }
//        node.put("logs", array);
//        
//        return node;
//    }


	
}
