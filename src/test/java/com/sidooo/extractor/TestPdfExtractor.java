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

import com.sidooo.extractor.PdfExtractor;
import com.sidooo.point.Item;

public class TestPdfExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException {
		File file = new File("src/test/resources/W020150106598588191582.pdf");
		InputStream stream = new FileInputStream(file);
		PdfExtractor extractor = new PdfExtractor(file.getPath());
		extractor.extract(stream);
		List<Item> contents = extractor.getItems();
		assertEquals(1, contents.size());
		assertEquals("W020150106598588191582", extractor.getTitle());
	}

}
