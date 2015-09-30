package com.sidooo.extractor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestContentExtractor {

	ExtractorManager manager; 
	@Before
	public void setUp() throws Exception {
		manager = new ExtractorManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		
		ContentExtractor extractor = null;
		String url = null;
		
		url = "http://test.com/abc/a.csv";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof CsvExtractor);
		
		url = "http://test.com/feing/b.xls";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof XlsExtractor);
		
		url = "http://test.com/feing/c.xlsx";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof XlsxExtractor);
		
		url = "http://test.com/feing/b.pdf";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof PdfExtractor);
		
		url = "http://test.com/feing/b.doc";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof DocExtractor);
		
		url = "http://test.com/feing/b.docx";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof DocxExtractor);
		
		url = "http://test.com/feing/b.html";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b.htm";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b.htm";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor == null);
		
		url = "http://test.com/feing/b/";
		extractor = manager.getInstanceByUrl(url);
		assertTrue(extractor == null);
	}

}
