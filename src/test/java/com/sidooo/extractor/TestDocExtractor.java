package com.sidooo.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.extractor.DocExtractor;
import com.sidooo.point.Item;

public class TestDocExtractor {
	
	private DocExtractor extractor = new DocExtractor();
	
	@Before
	public void setUp() throws Exception {
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() throws Exception {
		File file = new File("src/test/resources/陈华忠融资方案.doc");
		InputStream stream = new FileInputStream(file);
		
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		
		String item = null;
		List<String> items = new ArrayList<String>();
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		assertEquals("陈华忠融资方案", extractor.getTitle());
		item = items.get(0);
		String[] lines = item.split("\n");
		assertEquals(lines[0], "关于陈华忠融资方案分析报告");
		extractor.close();
	}
	
	@Test
	public void test2() throws Exception {
		File file = new File("src/test/resources/efd8bb13a3b949d1172f043405380165.doc");
		InputStream stream = new FileInputStream(file);
		extractor.setUrl(file.getPath());
		extractor.setInput(stream, null);
		
		String item = null;
		List<String> items = new ArrayList<String>();
		while((item = extractor.extract()) != null) {
			items.add(item);
		}
		assertEquals(1, items.size());
		assertEquals("efd8bb13a3b949d1172f043405380165", extractor.getTitle());
		item = items.get(0);
		String[] lines = item.split("\n");
		assertEquals(lines[0], "关于印发《劳动力市场职业分类与代码（LB501-2002）》的通知");
		extractor.close();
	}
	
	@Test
	public void test3() throws Exception {
		for(int i =0; i<5; i++) {
			test1();
			test2();
		}
	}

}
