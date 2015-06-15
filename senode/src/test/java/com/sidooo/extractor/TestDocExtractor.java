package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.DocExtractor;

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
		DocExtractor extractor = new DocExtractor(file.getPath(), stream);
		extractor.extract();
		String[] contents = extractor.getContents();
		assertEquals(1, contents.length);
		assertEquals("陈华忠融资方案", extractor.getTitle());
		String[] lines = contents[0].split("\n");
		assertEquals(lines[0], "关于陈华忠融资方案分析报告");
	}

}
