package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.DocExtractor;
import com.sidooo.point.Item;

public class TestDocExtractor {
	
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("src/test/resources/陈华忠融资方案.doc");
		InputStream stream = new FileInputStream(file);
		DocExtractor extractor = new DocExtractor();
		extractor.setUrl(file.getPath());
		extractor.extract(stream);
		List<Item> contents = extractor.getItems();
		assertEquals(1, contents.size());
		assertEquals("陈华忠融资方案", extractor.getTitle());
		String[] lines = contents.get(0).getContent().split("\n");
		assertEquals(lines[0], "关于陈华忠融资方案分析报告");
	}

}
