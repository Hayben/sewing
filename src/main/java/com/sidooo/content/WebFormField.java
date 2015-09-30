package com.sidooo.content;

public class WebFormField {
	private String title;
	
	private String value;
	
	public WebFormField(String title, String value) {
		this.title = title;
		this.value = value;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getValue() {
		return this.value;
	}
}
