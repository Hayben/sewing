package com.sidooo.crawl.instructment;

import org.jsoup.nodes.Element;


public class Var extends SelectInstructment{

	private String name;
	private String value;
	
	private SelectMethod method;
	private String key;
	private Integer index;
	private String attr;
	
	public Var(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public Var(String name, SelectMethod method, String key) {
		this.name = name;
		this.method = method;
		this.key = key;
		
	}
	
	public Var(String name, SelectMethod method, String key, Integer index) {
		this.name = name;
		this.method = method;
		this.key = key;
		this.index = index;
	}
	
	public Var(String name, SelectMethod method, String key, Integer index, String attribute) {
		this.name = name;
		this.method = method;
		this.key = key;
		this.index = index;
		this.attr = attribute;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public SelectMethod getMethod() {
		return this.method;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Integer getIndex() {
		return this.index;
	}
	
	@Override
	public boolean execute(Context context) {
		
		if (this.method != null) {
			String[] input = context.data.getContent();
			String[] output = select(method, key, index, attr, input);
			value = output[0];
		}
		
		context.data.setVariable(name, value);
		return true;
	}
	
	public static Var parse(Element node) {
		
		String name = node.attr("name");
		
		Var var;
		if (node.hasAttr("method")) {
			SelectMethod method = SelectMethod.valueOf(node.attr("method").toUpperCase());
			String key = node.attr("key");
			
			if (node.hasAttr("index")) {
				Integer index = Integer.parseInt(node.attr("index"));
				if (node.hasAttr("attribute")) {
					String attr = node.attr("attribute");
					var = new Var(name, method, key, index, attr);
				} else {
					var = new Var(name, method, key, index);
				}
				
			} else {
				var = new Var(name, method, key);
			}
		} else {
			var = new Var(name, node.toString());
		}
		
		return var;	
	}


}
