package com.sidooo.fetcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpFetcher extends Fetcher{
	
    private CloseableHttpClient client;
	
	public HttpFetcher(URL url, OutputStream out) {
		super(url, out);
		client = HttpClients.createDefault();
	}

	@Override
	public void fetch() throws Exception{
		
		HttpGet http = new HttpGet(url.toString());
		http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml,application/pdf;q=0.9,*/*;q=0.8");  
		http.addHeader("Connection", "Keep-Alive");  
		http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");  
		http.addHeader("Cookie", "");  
		
        CloseableHttpResponse response = null;
        try {

            response = client.execute(http);
            if (response == null) {
            	return;
            }
            
            int retCode = response.getStatusLine().getStatusCode();
            if (retCode >= 300) {
                return;
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
               return;
            }
            
            InputStream in = entity.getContent();
            byte[] buffer= new byte[16 * 1024];
            int size = 0;
            while((size = in.read(buffer))!=-1) {
            	out.write(buffer,0, size);
            }
            in.close();
            out.flush();
            

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
            if (response != null) {
            	try {
            		response.close();
            	} catch(Exception e) {
            		e.printStackTrace();
            	}
            } 
            
            client.close();
                
        }
	}
	
}
