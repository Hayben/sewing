package com.sidooo.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebForm {
	
	private String submitUrl;
	
	private Map<String, String> fields = new HashMap<String, String>();
	
	public void setSubmitUrl(String url) {
		this.submitUrl = url;
	}
	
	public String getSubmitUrl() {
		return this.submitUrl;
	}
	
	public void addField(String title) {
		this.fields.put(title, null);
	}
	
	public void addInput(String title, String value) {
		this.fields.put(title, value);
	}
	
	public String[] titles() {
		Set<String> titles = fields.keySet();
		return titles.toArray(new String[titles.size()]);
	}
	
	public WebFormField[] fields() {
		List<WebFormField> result = new ArrayList<WebFormField>();
		
		Set<String> titles = fields.keySet();
		for(String title : titles) {
			String value = fields.get(title);
			result.add(new WebFormField(title, value));
		}
		
		return result.toArray(new WebFormField[result.size()]);
	}
	
	public static WebForm from(HttpContent http) {
		WebForm form = new WebForm();
		
		return form;
	}

}
