package com.sidooo.point;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "link")
public class Link {
	
	@Id
	@Field("keyword")
    private String keyword;
	
	@Field("type")
	private String type;
    
	@Field("points")
	private List<String> points = new ArrayList<String>();
    
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
    
    public List<String> getPointList() {
    	return points;
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

	
}
