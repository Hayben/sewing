package com.sidooo.crawl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpFetcher extends Fetcher{
	
    private HttpClient client;
    
    private final int SMALL_FILE_MAX_SIZE = 25 * 1024 * 1024;
    
    private final int BUFFER_SIZE = 32 * 1024;
    
    private final int CONN_TIMEOUT = 10000;
    
    private final int READ_TIMEOUT = 90000;
	
	public HttpFetcher() {
		client = new DefaultHttpClient();
	}

	@Override
	public FetchContent fetch(String address) {
		
		FetchContent content = new FetchContent();
		URL url;
		try {
			url = new URL(address);
		} catch (MalformedURLException e1) {
			content.setStatus(190);
			return content;
		}
		
		HttpGet http = new HttpGet(url.toString());
		http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml,application/pdf;q=0.9,*/*;q=0.8");  
		http.addHeader("Connection", "Keep-Alive");  
		http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");  
		http.addHeader("Cookie", "");
		
		//设置连接超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONN_TIMEOUT);
		
		//设置读取数据超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
		
        HttpResponse response = null;
        try {

        	
            try {
				response = client.execute(http);
			} catch (ClientProtocolException e) {
				// 协议错误
				content.setStatus(197);
				return content;
			} catch (IOException e) {
				//连接错误
				content.setStatus(198);
				return content;
			}
            
            if (response == null) {
            	content.setStatus(196);
            	return content;
            }
                       
            int retCode = response.getStatusLine().getStatusCode();
            content.setStatus(retCode);
            
            if (retCode >= 300) {
                return content;
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	return content;
            }
            
            content.setMime(EntityUtils.getContentMimeType(entity));
            content.setCharset(EntityUtils.getContentCharSet(entity));
            content.setChunked(entity.isChunked());

            InputStream in;
			try {
				in = entity.getContent();
			} catch (IllegalStateException e) {
				content.setStatus(195);
				return content;
			} catch (IOException e) {
				//读取超时， 或者远端关闭
				content.setStatus(194);
				return content;
			}
            byte[] buffer= new byte[BUFFER_SIZE];
            long fileSize = entity.getContentLength();
            if (fileSize > SMALL_FILE_MAX_SIZE) {
            	content.setStatus(199);
            	return content;
            }
            	
        	//small file
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            int size = 0;
            try {
				while((size = in.read(buffer))!=-1) {
					out.write(buffer,0, size);
				}
			} catch (IOException e) {
				// 读取超时
				content.setStatus(193);
				return content;
			}
            
            content.setContent(out.toByteArray(), out.size());
            try {
				out.close();
			} catch (IOException e) {
			}
            
            try {
				in.close();
			} catch (IOException e) {
			}
            
            return content;
  

//            ContentType contentType = ContentType.get(entity);
//            Charset charset = contentType.getCharset();
//            if (charset == null) {
//                charset = Charset.defaultCharset();
//            }
//
//            //解决中文乱码
//            InputStreamReader isr = new InputStreamReader(entity.getContent(), charset);
//            BufferedReader reader = new BufferedReader(isr);
//            StringBuilder out = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                out.append(line+"\r\n");
//            }
//            
//            String content = new String(out.toString().getBytes(), "UTF-8");
//            this.out.write(content.getBytes());
            
            //this.html = new String(this.html.getBytes(charset), "UTF-8");

//            cookies.clear();
//            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
//            while(it.hasNext()) {
//                HeaderElement element = it.nextElement();
//                cookies.put(element.getName(), element.getValue());
//            }

        } finally {
        	
        	http.releaseConnection();
                
        }
	}
	
}
