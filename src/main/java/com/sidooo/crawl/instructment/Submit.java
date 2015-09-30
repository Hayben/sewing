package com.sidooo.crawl.instructment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sidooo.content.HttpContent;
import com.sidooo.content.WebFormField;

public class Submit extends BaseInstructment{

	private Map<String, String> inputs = new HashMap<String, String>();
	private String path;
	
	public Submit(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void addInput(String name, String value) {
		inputs.put(name, value);
	}
	
	public int getInputCount() {
		return inputs.size();
	}
	
	public String getInputName(int index) {
		
		Set<String> names = inputs.keySet();
		String[] array = names.toArray(new String[names.size()]);
		return array[index];
	}
	
	public String getInputValue(int index) {
		Collection<String> values = inputs.values();
		String[] array = values.toArray(new String[values.size()]);
		return array[index];
	}
	
	@Override
	public boolean execute(Context context) {
				
		String host = context.data.getVariable("host");
		
		JSONObject json = new JSONObject();
		Set<String> names = inputs.keySet();
		for(String name : names) {
			String value = inputs.get(name);
			if (value.startsWith("$")) {
				value = value.substring(1);
				value = context.data.getVariable(value);
			}
			json.put(name, value);
			//fields.add(new WebFormField(name, value));
			//context.data.setVariable(name, inputs.get(name));
		}
		
		HttpContent response = context.fetcher.post(host+path, json.toString());
		String[] content = { new String(response.getContent()) };
		context.data.setContent(content);
		return true;
	}
	
	public static Submit parse(Element node) {
		
		
		String path = node.attr("path");
		Submit submit = new Submit(path);
		
		Elements inputs = node.select("field");
		for(Element input : inputs) {
			
			String name = input.attr("name");
			String value = input.text();
			submit.addInput(name, value);
		}
		
		return submit;
	}


}
