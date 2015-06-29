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

@Document(collection = "link")
public class Link implements Writable{
	
	@Id
	@Field("keyword")
    private String keyword;
	
	@Field("type")
	private String type;
    
	@Field("points")
	private Set<String> points = new HashSet<String>();
    
    public String getKeyword() {
        return this.keyword;
    }
    
    public void setKeyword(String key) {
    	this.keyword = key;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getType() {
    	return this.type;
    }
    
    public String[] getPointList() {
    	return points.toArray(new String[points.size()]);
    }
    
    public void addPoint(String pointId) {
    	points.add(pointId);
    }
    
    public boolean existPoint(String pointId) {
    	for(String point : points) {
    		if (point.equals(pointId)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.keyword);
		out.writeUTF(this.type);
		out.writeInt(this.points.size());
		for(String point : points) {
			out.writeUTF(point);
		}
	}
	
	public static Link read(DataInput in) throws IOException {
		Link link = new Link();
		link.readFields(in);
		return link;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.keyword = in.readUTF();
		this.type = in.readUTF();
		this.points.clear();
		int count = in.readInt();
		for(int i=0; i<count; i++) {
			String point = in.readUTF();
			points.add(point);
		}
	}

	
}
