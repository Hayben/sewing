package com.sidooo.fetcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.crawl.FetchContent;
import com.sidooo.crawl.HttpFetcher;

public class TestHttpFetcher {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void saveContentToFile(FetchContent content, File file) throws Exception {
		
		OutputStream out = new FileOutputStream(file);
		out.write(content.getContent());
		out.close();
	}

	@Test
	public void testFetchHtml() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertTrue(content.getContentSize() > 0);
		assertEquals(content.getCharset(), "UTF-8");
		assertEquals(content.getType(), "text/html");
		File file = new File("index.html");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetchHtmlWithoutContentType() throws Exception {
		URL url = new URL("http://www.court.gov.cn/zgcpwsw/");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertTrue(content.getContentSize() > 0);
		assertEquals(content.getCharset(), "UTF-8");
		assertEquals(content.getType(), "text/html");
		File file = new File("zgcpwsw.html");
		saveContentToFile(content, file);
	}
	
	
	
	@Test
	public void testFetchPdf() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/n15816130.pdf");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 112406);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "application/pdf");
		File file = new File("n15816130.pdf");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetchDoc() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/陈华忠融资方案.doc");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 27648);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "application/msword");
		File file = new File("陈华忠融资方案.pdf");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetchDocx() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/20130411032037541.docx");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 44584);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		File file = new File("20130411032037541.docx");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetchCsv() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/shifenzheng-cdsgus_89.csv");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 3733833);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "text/csv");
		File file = new File("shifenzheng-cdsgus_89.csv");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetchXls() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/2398531.xls");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 675840);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "application/vnd.ms-excel");
		File file = new File("2398531.xls");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetch2M() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/62913229.PDF");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 1963742);	
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "application/pdf");
		File file = new File("62913229.pdf");
		saveContentToFile(content, file);
	}
	
	@Test
	public void testFetch37M() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/大学生（手机去重）.xlsx");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		assertEquals(content.getCharset(), "");
		assertEquals(content.getStatus(), 199);
		assertEquals(content.getContentSize(), 0);
	}
	
	@Test
	public void testFetch404() throws Exception {
		URL url = new URL("http://archive.sidooo.com/data/test/notexist.html");
		HttpFetcher fetcher = new HttpFetcher(url);
		FetchContent content = fetcher.fetch();
		assertEquals(content.getStatus(), 404);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getCharset(), "");
		assertEquals(content.getType(), "");
	}

}
