package com.sidooo.crawl;

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

import com.sidooo.content.HttpContent;
import com.sidooo.crawl.HttpFetcher;

public class TestHttpFetcher {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private void saveContentToFile(HttpContent content, File file)
			throws Exception {

		OutputStream out = new FileOutputStream(file);
		out.write(content.getContent());
		out.close();
	}

	@Test
	public void testFetchHtml() throws Exception {
		String url = "http://archive.sidooo.com/data/test/";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertTrue(content.getContentSize() > 0);
		assertEquals(content.getContentCharset(), "UTF-8");
		assertEquals(content.getContentType(), "text/html");
		// File file = new File("index.html");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchHtmlWithoutContentType() throws Exception {
		String url = "http://www.court.gov.cn/zgcpwsw/";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertTrue(content.getContentSize() > 0);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "text/html");
		// File file = new File("zgcpwsw.html");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchPdf() throws Exception {
		String url = "http://archive.sidooo.com/data/test/n15816130.pdf";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 112406);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "application/pdf");
		// File file = new File("n15816130.pdf");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchDoc() throws Exception {
		String url = "http://archive.sidooo.com/data/test/陈华忠融资方案.doc";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 27648);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "application/msword");
		// File file = new File("陈华忠融资方案.pdf");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchDocx() throws Exception {
		String url = "http://archive.sidooo.com/data/test/20130411032037541.docx";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 44584);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(),
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		// File file = new File("20130411032037541.docx");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchCsv() throws Exception {
		String url = "http://archive.sidooo.com/data/test/shifenzheng-cdsgus_89.csv";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 3733833);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "text/csv");
		// File file = new File("shifenzheng-cdsgus_89.csv");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchXls() throws Exception {
		String url = "http://archive.sidooo.com/data/test/2398531.xls";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 675840);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "application/vnd.ms-excel");
		// File file = new File("2398531.xls");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetch2M() throws Exception {
		String url = "http://archive.sidooo.com/data/test/62913229.PDF";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 200);
		assertEquals(content.getContentSize(), 1963742);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), "application/pdf");
		// File file = new File("62913229.pdf");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetch37M() throws Exception {
		String url = "http://archive.sidooo.com/data/test/大学生（手机去重）.xlsx";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getContentType(),
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getStatus(), 199);
		assertEquals(content.getContentSize(), 0);
	}

	@Test
	public void testFetch404() throws Exception {
		String url = "http://archive.sidooo.com/data/test/notexist2.html";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 404);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), "iso-8859-1");
		assertEquals(content.getContentType(), "text/html");
	}

	@Test
	public void testFetchNotExistHost() throws Exception {
		String url = "http://notexist.sidooo.com/notexist.html";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 198);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), null);
	}

	@Test
	public void testFetchNotSupportProcotol() throws Exception {
		String url = "https://archive.sidooo.com/notexist.html";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 198);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), null);
	}

	@Test
	public void testFetchURISyntaxError() throws Exception {
		String url = "http://www.test.com/###";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 191);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), null);
		assertEquals(content.getContentType(), null);
	}

	// Remote Address:120.132.73.233:80
	// Request URL:http://www.gzlm.net/down.php/info_VID_3472.html
	// Request Method:GET
	// Status Code:200 OK
	// Request Headersview source
	// Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
	// Accept-Encoding:gzip,deflate,sdch
	// Accept-Language:zh-CN,zh;q=0.8,en;q=0.6
	// Connection:keep-alive
	// Cookie:PHPSESSID=jr9erb3nhb8h3lgikgocl60vf7
	// Host:www.gzlm.net
	// User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML,
	// like Gecko) Chrome/38.0.2125.104 Safari/537.36
	// Response Headersview source
	// Accept-Ranges:bytes
	// Cache-Control:public
	// Connection:close
	// Content-Disposition:attachment;
	// filename="7fca5621807c831f1db41e2cbf76419d.tif"
	// Content-Type:application/force-download
	// Date:Tue, 07 Jul 2015 16:41:12 GMT
	// Expires:Thu, 19 Nov 1981 08:52:00 GMT
	// Pragma:no-cache
	// Server:Apache
	// Transfer-Encoding:chunked
	@Test
	public void testFetchDocNotExist() throws Exception {
		String url = "http://www.wxbt.gov.cn/btqzf/upload/ueditor/1436250224077.doc";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 404);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), "iso-8859-1");
		assertEquals(content.getContentType(), "text/html");
		// File file = new File("7fca5621807c831f1db41e2cbf76419d.tif");
		// saveContentToFile(content, file);
	}

	@Test
	public void testFetchLargePdft() throws Exception {
		String url = "http://jswx.spb.gov.cn/bmfw_3668/201402/P020140220416736846082.pdf";
		HttpFetcher fetcher = new HttpFetcher();
		HttpContent content = fetcher.get(url);
		assertEquals(content.getStatus(), 199);
		assertEquals(content.getContentSize(), 0);
		assertEquals(content.getContentCharset(), "iso-8859-1");
		assertEquals(content.getContentType(), "application/pdf");
		// File file = new File("7fca5621807c831f1db41e2cbf76419d.tif");
		// saveContentToFile(content, file);
	}

}
