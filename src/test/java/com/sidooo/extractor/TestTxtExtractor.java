package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.point.Item;

public class TestTxtExtractor {

	@Before
	public void setUp() throws Exception {
	}
	

	@After
	public void tearDown() throws Exception {
	}
	
	private void showMemorySize(int i) {
		long memSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(i + ":" +memSize);
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/1637_0.txt");
		InputStream stream = new FileInputStream(file);
		TxtExtractor extractor = new TxtExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		showMemorySize(1);
		int count = 0;
		String line = null;
		while((line = extractor.extract()) != null) {
			count ++;
		}
		showMemorySize(1);
		//List<Item> items = extractor.getItems();
		showMemorySize(1);
		assertEquals(40000, count);
		assertEquals("1637_0", extractor.getTitle());
	}
	
	@Test
	public void test2() throws Exception {
		showMemorySize(2);
		File file = new File("src/test/resources/1637_0.txt");
		InputStream stream = new FileInputStream(file);
		TxtExtractor extractor = new TxtExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		String line = null;
		int count = 0;
		while( (line = extractor.extract()) != null) {
			count ++;
		}
		assertEquals(40000, count);
		showMemorySize(2);
	}

}
