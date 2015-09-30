package com.sidooo.crawl.interrupt;

public class CaptchaInterrupt extends Interrupt {

	private String url;
	private byte[] image;
	
	public CaptchaInterrupt(String url, byte[] image) {
		this.url = url;
		this.image = image;
	}
	
	public byte[] getImage() {
		return this.image;
	}

	@Override
	public String getMessage() {
		return this.url;
	}
	
}
