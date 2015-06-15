package com.sidooo.point;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-1-10
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
@Document(collection = "point")
public class Point {
	
	//Document ID
	@Id
	@Field("id")
	private String docId;
	
	@Field("title")
	private String title;
	
	@Field("links")
	private List<String> links = new ArrayList<String>();
	
	public String getDocumentId() {
		return this.docId;
	}
	
	public void setDocumentId(String id) {
		this.docId = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<String> getLinks() {
		return links;
	}
	
	public void addLink(String linkId) {
		this.links.add(linkId);
	}
	
	public boolean existLink(String linkId) {
		for(String link : links) {
			if (link.equals(linkId)) {
				return true;
			}
		}
		
		return false;
	}

}
