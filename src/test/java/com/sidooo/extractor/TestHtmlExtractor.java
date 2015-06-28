package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
	public void test() throws FileNotFoundException {
		File file = new File("src/test/resources/进口葡萄酒抢占中国市场.html");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor(file.getPath());
		extractor.extract(stream);
		List<Item> items = extractor.getItems();
		assertEquals(1, items.size());
		Item item = items.get(0);
		assertEquals("进口葡萄酒抢占中国市场 国产酒面临份额危机_网易财经", item.getTitle());
	}
	
	@Test
	public void testExtractLinks() throws Exception {
		File file = new File("src/test/resources/httpdir.html");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor(file.toURL().toString());
		extractor.extractLink(stream, "utf8");
		String[] links = extractor.getLinks();
//		for(String link : links) {
//			System.out.println(link);
//		}
		assertEquals(94, links.length);
	}
	
	@Test
	public void TestExtractLinksGb2312() throws Exception {
		File file = new File("src/test/resources/中国裁判文书网.html");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor(file.toURL().toString());
		extractor.extractLink(stream, "gb2312");
		String[] links = extractor.getLinks();
		for(String link : links) {
			System.out.println(link);
		}
		assertEquals(71, links.length);
	}

}
