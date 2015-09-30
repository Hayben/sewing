package com.sidooo.crawl.instructment;

import org.jsoup.nodes.Element;

public class Refer extends BaseInstructment {

	private String refer;
	
	public Refer(String refer) {
		this.refer = refer;
	}
	
	public String getRefer() {
		return this.refer;
	}

	@Override
	public boolean execute(Context context) {
		String host = context.data.getVariable("host");
		context.fetcher.setRefer(host + this.refer);
		return true;
	}
	
	public static Refer parse(Element node) {
		String path = node.text();
		return new Refer(path);
	}
}
