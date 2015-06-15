package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.DocxExtractor;

public class TestDocxExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException {
		File file = new File("src/test/resources/P020140829352377824843.docx");
		InputStream stream = new FileInputStream(file);
		DocxExtractor extractor = new DocxExtractor(file.getPath(), stream);
		extractor.extract();
		String[] contents = extractor.getContents();
		assertEquals(1, contents.length);
		assertEquals("P020140829352377824843", extractor.getTitle());
		String[] lines = contents[0].trim().split("\n");
		assertEquals(lines[0], "广发内需增长灵活配置混合型证券投资基金");
	}

}
