package com.sidooo.crawl.memory;

import java.util.HashMap;
import java.util.Map;

import com.sidooo.content.HttpContent;

public class Data {
	
	private String[] content;
	private Map<String, String> variables = new HashMap<String,String>();
	
	public String getVariable(String variable) {
		return variables.get(variable);
	}

	public void setVariable(String name, String value) {
		variables.put(name, value);
	}
	
	public String[] getContent() {
		return this.content;
	}
	
	public void setContent(String[] content) {
		this.content = content;
	}
}
