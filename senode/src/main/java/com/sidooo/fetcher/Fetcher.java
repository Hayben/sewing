package com.sidooo.fetcher;

import java.io.OutputStream;
import java.net.URL;

abstract
public class Fetcher extends Thread{
	
	protected URL url;
	
	protected OutputStream out;
	
	protected String username;
	
	protected String password;

	public Fetcher(URL url, OutputStream out) {
		this.url = url;
		this.out = out;
	}
	
	public void setAccount(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public abstract void fetch() throws Exception;
	
	public static Fetcher getInstance(String urlPath, OutputStream stream) {
		
		URL url;
		try {
			url = new URL(urlPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if ("smb".equals(url.getProtocol())) {
			return new SambaFetcher(url, stream);
		} else if ("http".equals(url.getProtocol())) {
			return new HttpFetcher(url, stream);
		} else {
			return null;
		}
	}
}
