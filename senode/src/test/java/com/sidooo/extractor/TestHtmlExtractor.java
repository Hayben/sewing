package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.HtmlExtractor;

public class TestHtmlExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException {
		File file = new File("src/test/resources/进口葡萄酒抢占中国市场.html");
		InputStream stream = new FileInputStream(file);
		HtmlExtractor extractor = new HtmlExtractor(file.getPath(), stream);
		extractor.extract();
		String[] contents = extractor.getContents();
		assertEquals(1, contents.length);
		assertEquals("进口葡萄酒抢占中国市场 国产酒面临份额危机_网易财经", extractor.getTitle());
	}

}
