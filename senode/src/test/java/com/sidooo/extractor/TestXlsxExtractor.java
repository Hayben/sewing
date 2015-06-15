package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.XlsxExtractor;

public class TestXlsxExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException {
		File file = new File("src/test/resources/信贷客户资料.xlsx");
		InputStream stream = new FileInputStream(file);
		XlsxExtractor extractor = new XlsxExtractor(file.getPath(), stream);
		int count = 0;
		do {
			extractor.extract();
			String[] contents = extractor.getContents();
			count += contents.length;
		} while(!extractor.finished());
		
		assertEquals(1000, count);
		assertEquals("信贷客户资料", extractor.getTitle());
	}

}
