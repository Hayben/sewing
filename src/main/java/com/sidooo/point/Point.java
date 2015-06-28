package com.sidooo.point;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Writable;
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
public class Point implements Writable{
	
	//Document ID
	@Id
	@Field("id")
	private String docId;
	
	@Field("title")
	private String title;
	
	@Field("links")
	private Set<String> links;
	
	public Point() {
		docId = "";
		title = "";
		links = new HashSet<String>();
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
	
	public String[] getLinks() {
		return links.toArray(new String[links.size()]);
	}
	
	public void addLink(String linkId) {
		this.links.add(linkId);
	}
	
	public void removeLinks() {
		this.links.clear();
	}
	
	public boolean existLink(String linkId) {
		for(String link : links) {
			if (link.equals(linkId)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.docId);
		out.writeUTF(this.title);
		out.writeInt(this.links.size());
		for(String link : links) {
			out.writeUTF(link);
		}
 	}
	
	public static Point read(DataInput in) throws IOException {
		Point point = new Point();
		point.readFields(in);
		return point;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.links.clear();
		this.docId = in.readUTF();
		this.title = in.readUTF();
		int count = in.readInt();
		for(int i=0; i<count; i++) {
			String link = in.readUTF();
			this.links.add(link);
		}
	}

}
