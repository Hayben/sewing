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

import com.sidooo.extractor.DocxExtractor;
import com.sidooo.point.Item;

public class TestDocxExtractor {

	private DocxExtractor extractor = new DocxExtractor();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/P020140829352377824843.docx");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		String item = null;
		List<String> items = new ArrayList<String>();
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		assertEquals("P020140829352377824843", extractor.getTitle());
		item = items.get(0);
		String[] lines = item.split("\n");
		assertEquals(lines[4].trim(), "广发内需增长灵活配置混合型证券投资基金");
		extractor.close();
	}
	
	@Test
	public void test2() throws Exception {
		File file = new File("src/test/resources/P020150504528547183183.docx");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		String item = null;
		List<String> items = new ArrayList<String>();
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		assertEquals("P020150504528547183183", extractor.getTitle());
		item = items.get(0);
		
		String[] lines = item.split("\n");
		assertEquals(lines[5].trim(), "涟水县人民政府文件");
		extractor.close();
	}
	
	private void showMemorySize() {
		long memSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(memSize);
	}
	
	private void memtest1(DocxExtractor extractor) throws Exception {
		File file = new File("src/test/resources/P020150504528547183183.docx");
		InputStream stream = new FileInputStream(file);
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		String item = extractor.extract();
//		String[] lines = item.split("\n");
//		assertEquals(lines[5].trim(), "涟水县人民政府文件");
	}
	
	private void memtest2(DocxExtractor extractor) throws Exception {
		File file = new File("src/test/resources/P020140829352377824843.docx");
		InputStream stream = new FileInputStream(file);
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		String item = null;
		List<String> items = new ArrayList<String>();
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		assertEquals("P020140829352377824843", extractor.getTitle());
		item = items.get(0);
//		String[] lines = item.split("\n");
//		assertEquals(lines[4].trim(), "广发内需增长灵活配置混合型证券投资基金");
	}
	
	@Test
	public void test3() throws Exception {
		showMemorySize();
		memtest1(extractor);
		showMemorySize();
		memtest2(extractor);
		showMemorySize();
		memtest1(extractor);
		showMemorySize();
		memtest2(extractor);
		showMemorySize();
		extractor.close();
		showMemorySize();
	}

}
