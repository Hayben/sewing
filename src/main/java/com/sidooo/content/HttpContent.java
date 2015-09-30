package com.sidooo.content;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;

public class HttpContent implements Writable {

	public final static String TRANSFER_ENCODING = "Transfer-Encoding";
	public final static String CONTENT_ENCODING = "Content-Encoding";
	public final static String CONTENT_LANGUAGE = "Content-Language";
	public final static String CONTENT_LENGTH = "Content-Length";
	public final static String CONTENT_LOCATION = "Content-Location";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public final static String CONTENT_MD5 = "Content-MD5";
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String LAST_MODIFIED = "Last-Modified";
	public final static String LOCATION = "Location";
	public final static String SERVER = "Server";

	private long timestamp = System.currentTimeMillis();

	// 采集时服务器范围的Http Response Code
	private int status = 0;

	// HTTP应答头部
	private Map<String, String> headers = new HashMap<String, String>();

	//
	private long remoteSize = 0;
	
	// 采集数据所使用的网站帐号
	private String username = "";

	// 采集数据所使用的网站密码
	private String password = "";

	// 有些网站帐号需要提供额外的信息
	private String key = "";

	// 数据大小
	private long contentSize = 0;
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
	
	public void setRemoteSize(long remoteSize) {
		this.remoteSize = remoteSize;
	}
	
	public long getRemoteSize() {
		return this.remoteSize;
	}

	public void setContent(byte[] content, long size) {
		if (content.length != size) {
			this.contentSize = content.length ;
		} else {
			this.contentSize = size;
		}
		this.content = content;
	}

	public byte[] getContent() {
		return this.content;
	}

	public long getContentSize() {
		return this.contentSize;
	}

	public void addHeader(String name, String value) {
		this.headers.put(name, value);
	}
	
	public String getContentEncoding() {
		return headers.get(CONTENT_ENCODING);
	}

	public String getContentFilename() {
		String disposition = headers.get(CONTENT_DISPOSITION);
		if (disposition == null) {
			return null;
		}
		Header header = new BasicHeader(CONTENT_DISPOSITION, disposition);
		HeaderElement[] elements = header.getElements();
		for(HeaderElement element : elements) {
			NameValuePair pair = element.getParameterByName("filename");
			if (pair != null) {
				return pair.getValue();
			}
		}
		return null;
	}
	
	public String getContentType() {
		String contentType = headers.get(CONTENT_TYPE);
		if(contentType == null) {
			return null;
		}
		
		Header header = new BasicHeader(CONTENT_TYPE, contentType);
		HeaderElement[] elements = header.getElements();
		if (elements.length > 0) {
			return elements[0].getName();
		} else {
			return null;
		}
	}
	
	
	public String getContentCharset() {
		String contentType = headers.get(CONTENT_TYPE);
		if(contentType == null) {
			return null;
		}
		
		Header header = new BasicHeader(CONTENT_TYPE, contentType);
		HeaderElement[] elements = header.getElements();
		for(HeaderElement element : elements) {
			NameValuePair pair = element.getParameterByName("charset");
			if (pair != null) {
				return pair.getValue();
			}
		}
		return null;
	}
	
	
	public String getServer() {
		return headers.get(SERVER);
	}

	public static HttpContent read(DataInput in) throws IOException {
		HttpContent content = new HttpContent();
		content.readFields(in);
		return content;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.timestamp = in.readLong();
		this.status = in.readInt();
		this.headers.clear();
		int headerCount = in.readInt();
		for (int i = 0; i < headerCount; i++) {
			String name = in.readUTF();
			String value = in.readUTF();
			this.headers.put(name, value);
		}
		this.remoteSize = in.readLong();

		this.username = in.readUTF();
		this.password = in.readUTF();
		this.key = in.readUTF();
		this.contentSize = in.readLong();
		if (this.contentSize > 0) {
			this.content = new byte[(int) this.contentSize];
			in.readFully(this.content);
		} else {
			this.content = null;
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(timestamp);
		out.writeInt(status);
		out.writeInt(this.headers.size());
		Set<String> headerNames = this.headers.keySet();
		for (String headerName : headerNames) {
			String headerValue = this.headers.get(headerName);
			out.writeUTF(headerName);
			out.writeUTF(headerValue);
		}
		out.writeLong(this.remoteSize);

		out.writeUTF(this.username);
		out.writeUTF(this.password);
		out.writeUTF(this.key);
		out.writeLong(this.contentSize);
		if (this.contentSize > 0) {
			out.write(this.content);
		}
	}
}
