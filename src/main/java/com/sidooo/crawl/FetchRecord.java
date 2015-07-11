package com.sidooo.crawl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class FetchRecord implements WritableComparable<FetchRecord>{

	private String url;
	private long stamp;
	
	public FetchRecord() {
		
	}
	
	public FetchRecord(String url, long stamp) {
		this.url = url;
		this.stamp = stamp;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public long getStamp() {
		return this.stamp;
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(url);
		out.writeLong(stamp);
		
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.url = in.readUTF();
		this.stamp = in.readLong();
	}
	
	@Override
	public int compareTo(FetchRecord target) {
		return this.url.compareToIgnoreCase(target.getUrl());
	}
	
	
}
