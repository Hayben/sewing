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

import com.sidooo.extractor.XlsExtractor;
import com.sidooo.point.Item;

public class TestXlsExtractor {

	private XlsExtractor extractor = new XlsExtractor();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() throws Exception {
		File file = new File("src/test/resources/江苏省无锡市南长区企业老总手机号码通讯名录.xls");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		int count = 0;
		String line = null;
		while((line=extractor.extract()) != null) {
			count ++;
		};
		
		assertEquals(1496, count);
		assertEquals("江苏省无锡市南长区企业老总手机号码通讯名录", extractor.getTitle());
	}
	
	@Test
	public void test2() throws Exception {
		File file = new File("src/test/resources/第四十二批住房保障公示清册.xls");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		int count = 0;
		String line = null;
		while((line = extractor.extract()) != null) {
			count ++;
		};
		
		assertEquals(1030, count);
		assertEquals("第四十二批住房保障公示清册", extractor.getTitle());
	}

}
