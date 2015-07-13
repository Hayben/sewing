package com.sidooo.crawl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUri {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() throws URISyntaxException {
		URI uri = new URI("http://zengzhaozheng.blog.51cto.com/8219051/1379271");
		assertEquals(uri.getHost(), "zengzhaozheng.blog.51cto.com");
	}
	
	@Test
	public void test2() throws URISyntaxException {
		String url = "http://test.a.gov.cn/web/path/a.html";
		URI uri = new URI(url);
		String seedUrl = uri.getScheme() + "://" + uri.getHost() + "/";
		
		assertEquals(seedUrl, "http://test.a.gov.cn/");
	}
	
	@Test
	public void test3() throws IOException {
		Document doc = Jsoup.connect("http://www.hljrstbb.gov.cn/").timeout(5000).get();
		assertEquals(doc, null);
	}

}
