package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestContentDecompressor {

	private ContentDecompressor decompressor;
	
	@Before
	public void setUp() throws Exception {
		decompressor = new ContentDecompressor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		
		String gzFileName = "src/test/resources/test.tar.gz";
		decompressor.decompressGzipArchive(gzFileName);
		
		assertEquals(2391, (new File("src/test/resources/test/datatype.csv")).length());
		assertEquals(4551, (new File("src/test/resources/test/test2/iris.csv")).length());
		assertTrue((new File("src/test/resources/test/test2")).exists());
	}

}
