package com.sidooo.extractor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestContentExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		ContentExtractor extractor;
		String url = null;
		
		url = "http://test.com/abc/a.csv";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof CsvExtractor);
		
		url = "http://test.com/feing/b.xls";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof XlsExtractor);
		
		url = "http://test.com/feing/c.xlsx";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof XlsxExtractor);
		
		url = "http://test.com/feing/b.pdf";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof PdfExtractor);
		
		url = "http://test.com/feing/b.doc";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof DocExtractor);
		
		url = "http://test.com/feing/b.docx";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof DocxExtractor);
		
		url = "http://test.com/feing/b.html";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b.htm";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b.htm";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor instanceof HtmlExtractor);
		
		url = "http://test.com/feing/b";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor == null);
		
		url = "http://test.com/feing/b/";
		extractor = ContentExtractor.getInstanceByUrl(url);
		assertTrue(extractor == null);
	}

}
