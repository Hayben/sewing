package com.sidooo.division;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


//@Entity
//@Table(name="CHN", catalog="administrative_division")

@Document(collection="division")
public class Division {

	@Id
	private int id;
	
	@Field("name")
	private String 	name;
	
	@Field("level")
	private Integer level;
	
	@Field("parent")
	private int parent_id;

    public int getId() {
    	return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }

    public String getName() {
    	return this.name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public Integer getLevel() {
    	return this.level;
    }
    
    public void setLevel(Integer level) {
    	this.level = level;
    }
    
    public Integer getParentId() {
    	return this.parent_id;
    }
    
    public void setParentId(Integer parent_id) {
    	this.parent_id = parent_id;
    }
 
    
}
