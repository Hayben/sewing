package com.sidooo.crawl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpFetcher extends Fetcher{
	
    private HttpClient client;
    
    private final int SMALL_FILE_MAX_SIZE = 25 * 1024 * 1024;
    
    private final int BUFFER_SIZE = 32 * 1024;
	
	public HttpFetcher(URL url) {
		super(url);
		client = new DefaultHttpClient();
	}

	@Override
	public FetchContent fetch() throws Exception {
		
		HttpGet http = new HttpGet(url.toString());
		http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml,application/pdf;q=0.9,*/*;q=0.8");  
		http.addHeader("Connection", "Keep-Alive");  
		http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");  
		http.addHeader("Cookie", "");  
		
        HttpResponse response = null;
        try {

            response = client.execute(http);
            if (response == null) {
            	return null;
            }
            
            FetchContent content = new FetchContent();
            
            int retCode = response.getStatusLine().getStatusCode();
            content.setStatus(retCode);
            
            if (retCode >= 300) {
                return content;
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	return content;
            }
            
            content.setType(EntityUtils.getContentMimeType(entity));
            content.setCharset(EntityUtils.getContentCharSet(entity));
            content.setChunked(entity.isChunked());

            InputStream in = entity.getContent();
            byte[] buffer= new byte[BUFFER_SIZE];
            long fileSize = entity.getContentLength();
            if (fileSize > SMALL_FILE_MAX_SIZE) {
            	content.setStatus(199);
            	return content;
            }
            	
        	//small file
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            int size = 0;
            while((size = in.read(buffer))!=-1) {
            	out.write(buffer,0, size);
            }
            
            content.setContent(out.toByteArray(), out.size());
            out.close();
            in.close();
            
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

            
                
        }
	}
	
}
