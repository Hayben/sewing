package com.sidooo.crawl.instructment;

import org.jsoup.nodes.Element;

public class Select extends SelectInstructment{
	
	private SelectMethod method;
	private String key;
	private Integer index;
	private String attr;
	
	public Select(SelectMethod method, String key) {
		this.method = method;
		this.key = key;
	}
	
	public Select(SelectMethod method, String key, Integer index) {
		this.method = method;
		this.key = key;
		this.index = index;
	}
	
	public Select(SelectMethod method, String key, Integer index, String attr) {
		this.method = method;
		this.key = key;
		this.index = index;
		this.attr = attr;
	}

	@Override
	public boolean execute(Context context) {
		String[] content = context.data.getContent();
		
		String[] result = select(method, key, index, attr, content);
		context.data.setContent(result);
		return true;
	}
	
	public static Select parse(Element node) {
		
		SelectMethod method = SelectMethod.valueOf(node.attr("method").toUpperCase());
		String key = node.attr("key");
		
		Select select = null;
		if (node.hasAttr("index")) {
			Integer index = Integer.parseInt(node.attr("index"));
			if (node.hasAttr("attribute")) {
				String attr = node.attr("attribute");
				select = new Select(method, key, index, attr);
			} else {
				select = new Select(method, key, index);
			}
		} else {
			select = new Select(method, key);
		}
		return select;
	}

}
