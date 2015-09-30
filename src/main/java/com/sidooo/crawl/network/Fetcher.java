package com.sidooo.crawl.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.sidooo.content.HttpContent;

public class Fetcher {

	protected HttpClient client;

	private String refer;

	protected final int SMALL_FILE_MAX_SIZE = 10 * 1024 * 1024;

	protected final int BUFFER_SIZE = 32 * 1024;

	protected final int CONN_TIMEOUT = 5000;

	protected final int READ_TIMEOUT = 60000;
	
	public Fetcher() {
		
		client = new DefaultHttpClient();
		
		//设置连接超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONN_TIMEOUT);
		
		//设置读取数据超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
		
	}

	public void setRefer(String refer) {
		this.refer = refer;

	}
	
	public String getRefer() {
		return this.refer;
	}

	public HttpContent get(String address) {

		HttpContent content = new HttpContent();
		URI url;

		try {
			url = new URI(address);
		} catch (URISyntaxException e) {
			content.setStatus(190);
			return content;
		}

		HttpGet http;
		http = new HttpGet(url);

		http.addHeader(
				"Accept",
				"text/html,application/xhtml+xml,application/xml,application/pdf;q=0.9,*/*;q=0.8");
		http.addHeader("Connection", "Keep-Alive");
		http.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		http.addHeader("Cookie", "");
		
		if (this.refer != null) {
			http.addHeader("Referer", this.refer);
		}
		
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {

			HttpResponse response = null;
			try {
				response = client.execute(http);
			} catch (ClientProtocolException e) {
				// 协议错误
				content.setStatus(197);
				return content;
			} catch (IOException e) {
				// 连接错误
				content.setStatus(198);
				return content;
			} catch (IllegalStateException e) {
				System.out.println(address);
				System.out.println(url.toString());
				content.setStatus(191);
				return content;
			}

			if (response == null) {
				content.setStatus(196);
				return content;
			}

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				content.addHeader(header.getName(), header.getValue());
			}

			int retCode = response.getStatusLine().getStatusCode();
			content.setStatus(retCode);

			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return content;
			}

			long fileSize = entity.getContentLength();
			content.setRemoteSize(fileSize);
			if (fileSize > SMALL_FILE_MAX_SIZE) {
				content.setStatus(199);
				return content;
			}

			// content.setMime(EntityUtils.getContentMimeType(entity));
			// content.setCharset(EntityUtils.getContentCharSet(entity));

			if (retCode != 200) {
				return content;
			}

			try {
				in = entity.getContent();
			} catch (IllegalStateException e) {
				content.setStatus(195);
				return content;
			} catch (IOException e) {
				// 读取超时， 或者远端关闭
				content.setStatus(194);
				return content;
			}

			// small file
			out = new ByteArrayOutputStream();

			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				int size = 0;
				while ((size = in.read(buffer)) > 0) {
					out.write(buffer, 0, size);
				}
			} catch (IOException e) {
				// 读取超时
				content.setStatus(193);
				return content;
			}

			content.setContent(out.toByteArray(), out.size());

			return content;

			// ContentType contentType = ContentType.get(entity);
			// Charset charset = contentType.getCharset();
			// if (charset == null) {
			// charset = Charset.defaultCharset();
			// }
			//
			// //解决中文乱码
			// InputStreamReader isr = new
			// InputStreamReader(entity.getContent(), charset);
			// BufferedReader reader = new BufferedReader(isr);
			// StringBuilder out = new StringBuilder();
			// String line;
			// while ((line = reader.readLine()) != null) {
			// out.append(line+"\r\n");
			// }
			//
			// String content = new String(out.toString().getBytes(), "UTF-8");
			// this.out.write(content.getBytes());

			// this.html = new String(this.html.getBytes(charset), "UTF-8");

			// cookies.clear();
			// HeaderElementIterator it = new
			// BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
			// while(it.hasNext()) {
			// HeaderElement element = it.nextElement();
			// cookies.put(element.getName(), element.getValue());
			// }

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}

			http.releaseConnection();
			http = null;
		}
	}

	public HttpContent post(String address, String fields) {

		HttpContent content = new HttpContent();
		URI url;

		try {
			url = new URI(address);
		} catch (URISyntaxException e) {
			content.setStatus(190);
			return content;
		}

		HttpPost http;
		http = new HttpPost(url);

		http.addHeader(
				"Accept",
				"text/html,application/xhtml+xml,application/xml,application/pdf;q=0.9,*/*;q=0.8");
		http.addHeader("Connection", "Keep-Alive");
		http.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		http.addHeader("Cookie", "");
		
		if (this.refer != null) {
			http.addHeader("Referer", this.refer);
		}
		
		try {
			http.setEntity(new StringEntity(fields));
		} catch (UnsupportedEncodingException e1) {
			content.setStatus(189);
			return content;
		}

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {

			HttpResponse response = null;
			try {
				response = client.execute(http);
			} catch (ClientProtocolException e) {
				// 协议错误
				content.setStatus(197);
				return content;
			} catch (IOException e) {
				// 连接错误
				content.setStatus(198);
				return content;
			} catch (IllegalStateException e) {
				System.out.println(address);
				System.out.println(url.toString());
				content.setStatus(191);
				return content;
			}

			if (response == null) {
				content.setStatus(196);
				return content;
			}

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				content.addHeader(header.getName(), header.getValue());
			}

			int retCode = response.getStatusLine().getStatusCode();
			content.setStatus(retCode);

			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return content;
			}

			long fileSize = entity.getContentLength();
			content.setRemoteSize(fileSize);
			if (fileSize > SMALL_FILE_MAX_SIZE) {
				content.setStatus(199);
				return content;
			}

			// content.setMime(EntityUtils.getContentMimeType(entity));
			// content.setCharset(EntityUtils.getContentCharSet(entity));

			if (retCode != 200) {
				return content;
			}

			try {
				in = entity.getContent();
			} catch (IllegalStateException e) {
				content.setStatus(195);
				return content;
			} catch (IOException e) {
				// 读取超时， 或者远端关闭
				content.setStatus(194);
				return content;
			}

			// small file
			out = new ByteArrayOutputStream();

			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				int size = 0;
				while ((size = in.read(buffer)) > 0) {
					out.write(buffer, 0, size);
				}
			} catch (IOException e) {
				// 读取超时
				content.setStatus(193);
				return content;
			}

			content.setContent(out.toByteArray(), out.size());

			return content;

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}

			http.releaseConnection();
			http = null;
		}
	}


}
