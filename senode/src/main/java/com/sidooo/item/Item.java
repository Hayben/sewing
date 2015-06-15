package com.sidooo.item;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "item")
public class Item {
	
	@Id
	public String id;

	@Field("content")
	public String content;

	@Field("title")
	public String title;
	
	@Field("snap")
	public String snapUrl;
		
	@Field("origin")
	public String originUrl;
	
	@Field("seed")
	public String seed;

	
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
