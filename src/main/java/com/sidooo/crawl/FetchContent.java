package com.sidooo.crawl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FetchContent implements Writable {

	private long   timestamp = System.currentTimeMillis();
	private int	   status = 0;
	private String type = "";
	private String charset = "";
	private boolean chunked = false;
	private long   size = 0;
	private byte[] content = null;

	public long getTimeStamp() {
		return this.timestamp;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setType(String type) {
		if (type == null) {
			this.type = "";
		} else {
			this.type = type;
		}
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setCharset(String charset) {
		if (charset == null) {
			this.charset = "";
		} else {
			this.charset = charset;
		}
	}
	
	public String getCharset() {
		return this.charset;
	}
	
	public void setContent(byte[] content, long size) {
		this.size = size;
		this.content = content;
	}
	
	public byte[] getContent() {
		return this.content;
	}
	
	public long getContentSize() {
		return this.size;
	}
	
	public void setChunked(boolean chunked) {
		this.chunked = chunked;
	}
	
	public boolean getChunked() {
		return this.chunked;
	}

	public static FetchContent read(DataInput in) throws IOException {
		FetchContent content = new FetchContent();
		content.readFields(in);
		return content;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.timestamp = in.readLong();
		this.status = in.readInt();
		this.type = in.readUTF();
		this.charset = in.readUTF();
		this.chunked = in.readBoolean();
		this.size = in.readLong();
		if (this.size > 0) {
			this.content = new byte[(int)this.size];
			in.readFully(this.content);
		}
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(timestamp);
		out.writeInt(status);
		out.writeUTF(this.type);
		out.writeUTF(this.charset);
		out.writeBoolean(this.chunked);
		out.writeLong(this.size);
		if (this.size > 0) {
			out.write(this.content);
		}
	}



	
}
