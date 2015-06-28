package com.sidooo.crawl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FetchStatus implements Writable{
	
	private long   fetchTime = System.currentTimeMillis();
	private int	   status = 0;

	public long getFetchTime() {
		return fetchTime;
	}
	
	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}
	
	public void setStatus(int responseStatus) {
		this.status = responseStatus;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public static FetchStatus read(DataInput in) throws IOException {
		FetchStatus status = new FetchStatus();
		status.readFields(in);
		return status;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.fetchTime = in.readLong();
		this.status = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(this.fetchTime);
		out.writeInt(this.status);
	}

}
