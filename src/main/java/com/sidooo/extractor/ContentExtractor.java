package com.sidooo.extractor;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.sidooo.point.Item;


public abstract class ContentExtractor {

	private String title;
	
	private List<Item> items = new ArrayList<Item>();
	
	private Set<String> links = new HashSet<String>();
	
	protected String path;
	
	protected boolean reachEnd = false;
	
	public ContentExtractor(String path) {
		this.path = path;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	protected void addItem(String content) {
		Item item = new Item();
		item.setTitle(title);
		item.setUrl(path);
		item.setContent(content);
		items.add(item);
	}
	
	protected void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	protected void clearItems() {
		items.clear();
	}
	
	public String[] getLinks() {
		return links.toArray(new String[links.size()]);
	}
	
	protected void addLink(String link) {
		links.add(link);
	}
	
	protected void clearLinks() {
		links.clear();
	}
	
	abstract
	public void extract(InputStream input);
	
	public boolean finished() {
		return reachEnd;
	}
	
	protected void finish() {
		reachEnd = true;
	}

	public static ContentExtractor getInstance(String path) {
		
		String format = FilenameUtils.getExtension(path);
		if (format == null) {
			return null;
		}
		
		if ("html".equalsIgnoreCase(format) || 
			"htm".equalsIgnoreCase(format)) {
			return new HtmlExtractor(path);
		} else if ("csv".equalsIgnoreCase(format)) {
			return new CsvExtractor(path);
		} else if ("xls".equalsIgnoreCase(format)){
			return new XlsExtractor(path);
		} else if ("xlsx".equalsIgnoreCase(format)) {
			return new XlsxExtractor(path);
		} else if ("doc".equalsIgnoreCase(format)) {
			return new DocExtractor(path);
		} else if ("docx".equalsIgnoreCase(format)) {
			return new DocxExtractor(path);
		} else if ("pdf".equalsIgnoreCase(format)) {
			return new PdfExtractor(path);
		} else {
			return null;
		}
	}



}
