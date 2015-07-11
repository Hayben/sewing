package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLinkExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
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
	public void testRefLink() throws Exception {
		File file = new File("src/test/resources/ref.html");
		InputStream stream = new FileInputStream(file);
		LinkExtractor extractor = new LinkExtractor();
		extractor.setUrl(file.toURL().toString());
		extractor.setInput(stream, "utf8");
		
		List<String> links = new ArrayList<String>();
		String link = null;
		while((link = extractor.extract()) != null) {
			links.add(link);
		}
		assertEquals(1, links.size());
		assertEquals("http://www.test.org/conf", links.get(0));
	}
}
