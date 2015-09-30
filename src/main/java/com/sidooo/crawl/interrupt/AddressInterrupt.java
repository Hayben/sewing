package com.sidooo.crawl.interrupt;

public class AddressInterrupt extends Interrupt {

	private int address;
	
	public AddressInterrupt(int address) {
		this.address = address;
	}
	
	@Override
	public String getMessage() {
		return "Invalid Address: " + address;
	}

}
