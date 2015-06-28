package com.sidooo.ai;


public class Attribute {
	
	private String id;
	
	private String name;
	
	private String rule;
	
	private boolean enabled;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRule() {
		return this.rule;
	}
	
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public void enable(boolean enable) {
		this.enabled = enable;
	}
	
	public boolean enabled() {
		return this.enabled;
	}

}