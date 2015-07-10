package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.HtmlExtractor;
import com.sidooo.point.Item;

public class TestHtmlExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/进口葡萄酒抢占中国市场.html");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		List<String> items = new ArrayList<String>();
		
		String item = null;
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		item = items.get(0);
		assertTrue(item.length() > 0);
		//assertEquals("进口葡萄酒抢占中国市场 国产酒面临份额危机_网易财经", item.getTitle());
	}
	
	@Test
	public void testExtractLinks() throws Exception {
		File file = new File("src/test/resources/httpdir.html");
		InputStream stream = new FileInputStream(file);
		LinkExtractor extractor = new LinkExtractor();
		extractor.setUrl(file.toURL().toString());
		extractor.setInput(stream, "utf8");
		
		List<String> links = new ArrayList<String>();
		String link = null;
		while((link = extractor.extract()) != null) {
			links.add(link);
		}
		assertEquals(94, links.size());
	}
	
	@Test
	public void TestExtractLinksGb2312() throws Exception {
		File file = new File("src/test/resources/中国裁判文书网.html");
		InputStream stream = new FileInputStream(file);
		LinkExtractor extractor = new LinkExtractor();
		extractor.setUrl(file.toURL().toString());
		extractor.setInput(stream, "gb2312");
		
		List<String> links = new ArrayList<String>();
		String link = null;
		while((link = extractor.extract()) != null) {
			links.add(link);
		}
		assertEquals(139, links.size());
	}
	
	@Test
	public void TestExtractTif() throws Exception {
		File file = new File("src/test/resources/7fca5621807c831f1db41e2cbf76419d.tif");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		List<String> items = new ArrayList<String>();
		String item = null;
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		item = items.get(0);
		assertTrue(item.length() > 0);
	}
	

}
