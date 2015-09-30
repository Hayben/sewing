package com.sidooo.crawl.cpu;

public class Register {
	
	public int eip;
	
	public void setCursor(int newAddress) {
		this.eip = newAddress;
	}
	
	public int getCursor() {
		return this.eip;
	}


}
