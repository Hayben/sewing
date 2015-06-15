package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.XlsExtractor;

public class TestXlsExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() throws FileNotFoundException {
		File file = new File("src/test/resources/江苏省无锡市南长区企业老总手机号码通讯名录.xls");
		InputStream stream = new FileInputStream(file);
		XlsExtractor extractor = new XlsExtractor(file.getPath(), stream);
		int count = 0;
		do {
			extractor.extract();
			String[] contents = extractor.getContents();
			count += contents.length;
		} while(!extractor.finished());
		
		assertEquals(1494, count);
		assertEquals("江苏省无锡市南长区企业老总手机号码通讯名录", extractor.getTitle());
	}
	
	@Test
	public void test2() throws Exception {
		File file = new File("src/test/resources/第四十二批住房保障公示清册.xls");
		InputStream stream = new FileInputStream(file);
		XlsExtractor extractor = new XlsExtractor(file.getPath(), stream);
		int count = 0;
		do {
			extractor.extract();
			String[] contents = extractor.getContents();
			count += contents.length;
		} while(!extractor.finished());
		
		assertEquals(591, count);
		assertEquals("第四十二批住房保障公示清册", extractor.getTitle());
	}

}
