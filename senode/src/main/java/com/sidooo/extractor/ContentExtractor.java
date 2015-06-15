package com.sidooo.extractor;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;


public abstract class ContentExtractor {

	private String title;
	
	private List<String> items = new ArrayList<String>();
	
	private Set<String> links = new HashSet<String>();
	
	protected String path;
	
	protected InputStream stream;
	
	protected boolean reachEnd = false;
	
	public ContentExtractor(String path, InputStream stream) {
		this.path = path;
		this.stream = stream;
	}
	
	public String[] getContents() {
		return items.toArray(new String[items.size()]);
	}
	
	protected void addContent(String content) {
		items.add(content);
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
	public void extract();
	
	public boolean finished() {
		return reachEnd;
	}
	
	protected void finish() {
		reachEnd = true;
	}

	public static ContentExtractor getInstance(String path, InputStream stream) {
		
		String format = FilenameUtils.getExtension(path);
		if (format == null) {
			return null;
		}
		
		if ("html".equalsIgnoreCase(format) || 
			"htm".equalsIgnoreCase(format)) {
			return new HtmlExtractor(path, stream);
		} else if ("csv".equalsIgnoreCase(format)) {
			return new CsvExtractor(path, stream);
		} else if ("xls".equalsIgnoreCase(format)){
			return new XlsExtractor(path, stream);
		} else if ("xlsx".equalsIgnoreCase(format)) {
			return new XlsxExtractor(path, stream);
		} else if ("doc".equalsIgnoreCase(format)) {
			return new DocExtractor(path, stream);
		} else if ("docx".equalsIgnoreCase(format)) {
			return new DocxExtractor(path, stream);
		} else if ("pdf".equalsIgnoreCase(format)) {
			return new PdfExtractor(path, stream);
		} else {
			return null;
		}
	}



}
