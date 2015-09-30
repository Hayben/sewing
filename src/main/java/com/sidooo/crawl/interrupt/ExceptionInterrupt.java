package com.sidooo.crawl.interrupt;

public class ExceptionInterrupt extends Interrupt{

	private int address;
	private String instructment;
	private String error;
	
	public ExceptionInterrupt(int address, String instructment, String error) {
		this.address = address;
		this.instructment = instructment;
		this.error = error;
	}
	
	@Override
	public String getMessage() {
		return "Address:" + address + 
				" Instructment:" + instructment +
				" Error: " + error;
	}

}
