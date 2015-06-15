package com.sidooo.fetcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHttpFetcher {

	File file;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		
		if (file != null) {
			file.delete();
		}
	}

	@Test
	public void testFetchHtml() throws Exception {
		URL url = new URL("http://www.baidu.com/");
		file = new File("baidu.html");
		OutputStream out = new FileOutputStream(file);
		HttpFetcher fetcher = new HttpFetcher(url, out);
		fetcher.fetch();
		out.close();
		
		assertTrue(file.exists());
		assertTrue(file.length() > 0);
	}
	
	@Test
	public void testFetchPdf() throws Exception {
		URL url = new URL("http://www.miit.gov.cn/n11293472/n11295091/n11299329/n15816131.files/n15816130.pdf");
		file = new File("n15816130.pdf");
		OutputStream out = new FileOutputStream(file);
		HttpFetcher fetcher = new HttpFetcher(url, out);
		fetcher.fetch();
		out.close();
		
		assertTrue(file.exists());
		assertEquals(file.length(), 112406);	
	}
	
	@Test
	public void testFetchLargeFile() throws Exception {
		URL url = new URL("http://www.cninfo.com.cn/finalpage/2013-08-07/62913229.PDF");
		file = new File("62913229.pdf");
		OutputStream out = new FileOutputStream(file);
		HttpFetcher fetcher = new HttpFetcher(url, out);
		fetcher.fetch();
		out.close();
		
		assertTrue(file.exists());
		assertEquals(file.length(), 1963742);	
	}
	
	@Test
	public void testFetchXls() throws Exception {
		URL url = new URL("http://www.miit.gov.cn/n11293472/n11293832/n11293907/n11368223/n13663971.files/n13663981.xls");
		file = new File("n13663981.xls");
		OutputStream out = new FileOutputStream(file);
		HttpFetcher fetcher = new HttpFetcher(url, out);
		fetcher.fetch();
		out.close();
		
		assertTrue(file.exists());
		assertEquals(file.length(), 42496);
	}
	
	@Test
	public void testFetchDoc() throws Exception {
		URL url = new URL("http://www.zjt.gov.cn/module/download/downfile.jsp?classid=0&filename=100422150340281.doc");
		file = new File("100422150340281.doc");
		OutputStream out = new FileOutputStream(file);
		HttpFetcher fetcher = new HttpFetcher(url, out);
		fetcher.fetch();
		out.close();
		
		assertTrue(file.exists());
		assertEquals(file.length(), 103424);
	}

}
