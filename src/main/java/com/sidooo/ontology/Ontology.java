package com.sidooo.ontology;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "ontology")
public class Ontology {
	
	@Id
	private String id;
	
	@Field("title")
	private String title;
	
	@Field("description")
	private String description;
	
	@Field("category")
	private String category;
	
	@Field("members")
	private List<Member> members = new ArrayList<Member>();
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String name) {
		this.title = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public List<Member> getMembers() {
		return members;
	}
}
