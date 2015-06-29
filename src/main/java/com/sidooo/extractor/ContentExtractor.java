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
	
	public void setUrl(String url) {
		this.path = url;
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

	public static ContentExtractor getInstanceByUrl(String path) {
		
		String format = FilenameUtils.getExtension(path);
		if (format == null) {
			return null;
		}
		
		if ("html".equalsIgnoreCase(format) || 
			"htm".equalsIgnoreCase(format)) {
			return new HtmlExtractor();
		} else if ("csv".equalsIgnoreCase(format)) {
			return new CsvExtractor();
		} else if ("xls".equalsIgnoreCase(format)){
			return new XlsExtractor();
		} else if ("xlsx".equalsIgnoreCase(format)) {
			return new XlsxExtractor();
		} else if ("doc".equalsIgnoreCase(format)) {
			return new DocExtractor();
		} else if ("docx".equalsIgnoreCase(format)) {
			return new DocxExtractor();
		} else if ("pdf".equalsIgnoreCase(format)) {
			return new PdfExtractor();
		} else if ("txt".equalsIgnoreCase(format)) {
			return new TxtExtractor();
		} else {
			return null;
		}
	}
	
	public static ContentExtractor getInstanceByMime(String mime) {
		
		if ("text/html".equalsIgnoreCase(mime)) {
			return new HtmlExtractor();
		} else if ("text/csv".equalsIgnoreCase(mime)) {
			return new CsvExtractor();
		} else if ("text/plain".equalsIgnoreCase(mime)) {
			return new TxtExtractor();
		} else if ("application/pdf".equalsIgnoreCase(mime)) {
			return new PdfExtractor();
		} else if ("application/msword".equalsIgnoreCase(mime)) {
			return new DocExtractor();
		} else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(mime)) {
			return new DocxExtractor();
		} else if ("application/vnd.ms-excel".equalsIgnoreCase(mime)) {
			return new XlsExtractor();
		} else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(mime)) {
			return new XlsxExtractor();
		} else {
			return null;
		}
	}
	
	public static ContentExtractor getInstanceByContent(byte[] content) {
		
		ContentDetector detector = new ContentDetector();
		ContentType ct = detector.detect(content);
		if (ct == null) {
			return null;
		}
		
		if (ct.mime == null || ct.mime.length() <= 0) {
			return null;
		}
		
		return getInstanceByMime(ct.mime);
	}



}
