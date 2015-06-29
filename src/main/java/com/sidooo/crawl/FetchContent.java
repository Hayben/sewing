package com.sidooo.crawl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FetchContent implements Writable {

	private long   timestamp = System.currentTimeMillis();
	
	//采集时服务器范围的Http Response Code
	private int	   status = 0;
	
	//mime
	private String mime = "";
	
	//内容编码集
	private String charset = "";
	
	private boolean chunked = false;
	
	//网页的最后更新日期
	private long  date = 0;   
	
	//采集数据所使用的网站帐号
	private String username = "";	
	
	//采集数据所使用的网站密码
	private String password = "";
	
	//有些网站帐号需要提供额外的信息
	private String key = "";
		
	//数据大小
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
	
	public void setMime(String type) {
		if (type == null) {
			this.mime = "";
		} else {
			this.mime = type;
		}
	}
	
	public String getMime() {
		return this.mime;
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
		this.mime = in.readUTF();
		this.charset = in.readUTF();
		this.chunked = in.readBoolean();
		this.date = in.readLong();
		this.username = in.readUTF();
		this.password = in.readUTF();
		this.key = in.readUTF();
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
		out.writeUTF(this.mime);
		out.writeUTF(this.charset);
		out.writeBoolean(this.chunked);
		out.writeLong(this.date);
		out.writeUTF(this.username);
		out.writeUTF(this.password);
		out.writeUTF(this.key);
		out.writeLong(this.size);
		if (this.size > 0) {
			out.write(this.content);
		}
	}



	
}
