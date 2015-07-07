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

import com.sidooo.extractor.XlsxExtractor;
import com.sidooo.point.Item;

public class TestXlsxExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/信贷客户资料.xlsx");
		InputStream stream = new FileInputStream(file);
		XlsxExtractor extractor = new XlsxExtractor();
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		int count = 0;
		String line = null;
		while((line = extractor.extract()) != null) {
			count ++;
		};
		
		assertEquals(1008, count);
		assertEquals("信贷客户资料", extractor.getTitle());
	}

}
