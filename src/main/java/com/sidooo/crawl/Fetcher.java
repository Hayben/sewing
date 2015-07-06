package com.sidooo.crawl;

import java.io.OutputStream;
import java.net.URL;

import org.apache.hadoop.fs.FileSystem;

import com.sidooo.seed.Account;

abstract
public class Fetcher extends Thread{
	
	protected String username;
	
	protected String password;
	
	public void setAccount(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public abstract FetchContent fetch(String url);
	
	public static Fetcher getInstance(String urlPath) {
		
		URL url;
		try {
			url = new URL(urlPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		

		if ("http".equals(url.getProtocol())) {
			return new HttpFetcher();
		} else {
			return null;
		}
	}
}
