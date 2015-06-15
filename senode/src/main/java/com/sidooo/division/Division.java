package com.sidooo.division;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="CHN", catalog="administrative_division")
public class Division {
	
	private Integer id;
	private String 	name;
	private Integer level;
	private Integer parent_id;
	
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
    	return id;
    }
    
    public void setId(Integer id) {
    	this.id = id;
    }
    
    @Column(name = "name", nullable = false)
    public String getName() {
    	return this.name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    @Column(name = "level", nullable = false)
    public Integer getLevel() {
    	return this.level;
    }
    
    public void setLevel(Integer level) {
    	this.level = level;
    }
    
    @Column(name = "parentid", nullable = false)
    public Integer getParentId() {
    	return this.parent_id;
    }
    
    public void setParentId(Integer parent_id) {
    	this.parent_id = parent_id;
    }
 
    
}
