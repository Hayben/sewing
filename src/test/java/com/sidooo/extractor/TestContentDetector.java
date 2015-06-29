package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sidooo.point.Item;

public class TestContentDetector {

	private ContentDetector detector = new ContentDetector();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHtmlWithoutContentType() throws Exception {
		File file = new File("src/test/resources/中国裁判文书网.html");
		//File file = new File("src/test/resources/httpdir.html");
		FileInputStream stream = new FileInputStream(file);
		byte[] content = new byte[1*1024*1024];
		stream.read(content);
		ContentType type = detector.detect(content);
		assertEquals(type.mime, "text/html");
		assertEquals(type.charset, "gb2312");
	}
	
	@Test
	public void testPdf() throws Exception {
		File file = new File("src/test/resources/W020150106598588191582.pdf");
		//File file = new File("src/test/resources/httpdir.html");
		FileInputStream stream = new FileInputStream(file);
		byte[] content = new byte[1*1024*1024];
		stream.read(content);
		ContentType type = detector.detect(content);
		assertEquals(type.mime, "application/pdf");
		assertEquals(type.charset, null);
	}
	
	@Test
	public void testCsv() throws Exception {
		File file = new File("src/test/resources/7k7k.csv");
		//File file = new File("src/test/resources/httpdir.html");
		FileInputStream stream = new FileInputStream(file);
		byte[] content = new byte[1*1024*1024];
		stream.read(content);
		ContentType type = detector.detect(content);
		assertEquals(type.mime, "text/plain");
		assertEquals(type.charset, "utf-8");
	}

}
