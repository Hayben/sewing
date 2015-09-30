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

import com.sidooo.extractor.PdfExtractor;

public class TestPdfExtractor {

	private PdfExtractor extractor = new PdfExtractor();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void showMemorySize() {
		long memSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(memSize);
	}

	@Test
	public void test1() throws Exception {
		File file = new File("src/test/resources/W020150106598588191582.pdf");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		List<String> items = new ArrayList<String>();
		String line = null;
		while((line = extractor.extract()) != null) {
			items.add(line);
		}
		
		assertEquals(1, items.size());
		assertEquals("W020150106598588191582", extractor.getTitle());
	}
	
	@Test
	public void test2() throws Exception {
		showMemorySize();
		File file = new File("src/test/resources/P020131106536628044966.pdf");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		showMemorySize();
		extractor.setInput(stream, null);
		List<String> items = new ArrayList<String>();
		String line = null;
		while((line = extractor.extract()) != null) {
			items.add(line);
		}
		
		assertEquals(1, items.size());
		assertEquals("P020131106536628044966", extractor.getTitle());
		showMemorySize();
	}
	
	@Test
	public void testLoop() throws Exception {
		test1();
		test2();
	}

}
