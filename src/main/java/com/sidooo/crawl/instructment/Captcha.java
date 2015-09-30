package com.sidooo.crawl.instructment;


import org.jsoup.nodes.Element;

import com.sidooo.content.HttpContent;


public class Captcha extends SelectInstructment{

	private byte[] image;
	private String url;
	
	private SelectMethod method;
	private String key;
	private Integer index;
	private String attr;
	
	public Captcha(SelectMethod method, String key) {
		this.method = method;
		this.key = key;
	}
	
	public Captcha(SelectMethod method, String key, Integer index) {
		this.method = method;
		this.key = key;
		this.index = index;
	}
	
	public Captcha(SelectMethod method, String key, Integer index, String attribute) {
		this.method = method;
		this.key = key;
		this.index = index;
		this.attr = attribute;
	}
	
	public byte[] getImage() {
		return this.image;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean execute(Context context) {
		String host = context.data.getVariable("host");
		
		String image_path = null;
		if (method != null) {
			String[] input = context.data.getContent();
			String[] output = select(method, key, index, attr, input);
			image_path = output[0];
		}
		
		HttpContent content = context.fetcher.get(host + image_path);
		this.image = content.getContent();
		context.flag.interrupted = true;
		return true;
	}
	
	public static Captcha parse(Element node) {
		
		SelectMethod method = SelectMethod.valueOf(node.attr("method").toUpperCase());
		String key = node.attr("key");
		
		Captcha captcha = null;
		if (node.hasAttr("index")) {

			
			Integer index = Integer.parseInt(node.attr("index"));
			if (node.hasAttr("attribute")) {
				String attr = node.attr("attribute");
				captcha = new Captcha(method, key, index, attr);
			} else {
				captcha = new Captcha(method, key, index);
			}
		} else {
			captcha = new Captcha(method, key);
		}
		return captcha;
	}
	
}
