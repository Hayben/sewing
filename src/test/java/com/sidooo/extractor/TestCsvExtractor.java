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

import com.sidooo.extractor.CsvExtractor;
import com.sidooo.point.Item;

public class TestCsvExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/7k7k.csv");
		InputStream stream = new FileInputStream(file);
		CsvExtractor extractor = new CsvExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		int count = 0;
		String item = null;
		while((item = extractor.extract()) != null) {
			count++;
		}
		
		assertEquals(6000, count);
		assertEquals("7k7k", extractor.getTitle());
	}

}
