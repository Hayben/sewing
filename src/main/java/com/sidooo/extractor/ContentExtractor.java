package com.sidooo.extractor;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;


public abstract class ContentExtractor {

	protected final int MAX_SIZE = 10 * 1024 * 1024;
	
	private String title;
	
	protected String path;
	
	public void setUrl(String url) {
		this.path = url;
	}
	
	protected void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	abstract
	public void setInput(InputStream input, String charset) throws Exception;
	
	abstract
	public String extract();
	
	abstract
	public void close();




}
