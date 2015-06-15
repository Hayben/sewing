package com.sidooo.fetcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SambaFetcher extends Fetcher{
	
	public SambaFetcher(URL url, OutputStream out) {
		super(url, new BufferedOutputStream(out));
	}

	private final int BUFFER_SIZE = 16 * 1024;
	
	@Override
	public void fetch() throws Exception{
		
		String host = url.getHost();
		
		NtlmPasswordAuthentication auth = 
				new NtlmPasswordAuthentication(host, username, password); 

		BufferedInputStream in = null;
		try {
				
			SmbFile inFile = new SmbFile(this.url, auth);
			in = new BufferedInputStream(new SmbFileInputStream(inFile));

			byte[] buffer = new byte[BUFFER_SIZE];
			int size = 0;
			while ((size = in.read(buffer))!= -1) {
				out.write(buffer, 0, size);
				out.flush();
			}

		} finally {

			if (in!= null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
			
			
	}
}
