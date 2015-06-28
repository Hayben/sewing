package com.sidooo.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.ai.IDKeyword;
import com.sidooo.ai.Recognition;

import junit.framework.TestCase;

public class TestRecognition extends TestCase {
	
	private Recognition recog = new Recognition();
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	private String readFile(File file) {
		String content = "";
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(file), "UTF-8"));
			String line;
			while((line = reader.readLine()) != null) {
				content += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				
			}
		}
		
		return content;
	}
	
	@Test
	public void testMobile() {
		assertEquals("mobile", recog.match("13682827121"));
	}
	
	@Test
	public void testTelephone() {
		assertEquals("tel", recog.match("0510-85737188"));
		assertEquals("tel", recog.match("010-62381410-125"));
		assertTrue("tel" != recog.match("310105820907209"));
	}
	
	@Test
	public void testPassport() {
		assertEquals("passport", recog.match("P1234567"));
	}
	
	@Test
	public void testMilo() {
		assertEquals("milo", recog.match("政字第00111206号"));
	}
	
	@Test
	public void testUrl() {
		assertEquals("url", recog.match("http://www.cnblogs.com/jay-xu33/archive/2009/01/08/1371953.html"));
	}
	
	@Test
	public void testEmail() {
		assertEquals("email", recog.match("markus.sprunck@online.de"));
		assertEquals("email", recog.match("test@sina.com"));
		assertEquals("email", recog.match("sadfsd@sdfsd.cn"));
		assertEquals("email", recog.match("analyst@gmail.com"));
		assertEquals("email", recog.match("abc@sina.com.cn"));
	}
	
	@Test
	public void testOrganization() {
		assertEquals("org", recog.match("无锡斯特精密机械有限公司"));
		assertEquals("org", recog.match("无锡东晶玻璃制品有限公司"));
		assertEquals("org", recog.match("江阴市灵灵机械制造有限公司"));
		//assertEquals("org", recog.match("江苏艇王模塑有限公司"));
		assertEquals("org", recog.match("恒大祥贸易公司"));
		//assertEquals("org", recog.match("余姚长江温控仪表厂"));
		//assertEquals("org", recog.match("无锡市雄风型钢有限公司"));
		//assertEquals("org", recog.match("无锡市惠落机械厂"));
		assertEquals("org", recog.match("无锡中联广告传媒有限公司"));
		assertEquals("org", recog.match("无锡市天茂胶辊有限公司"));
		//assertEquals("org", recog.match("中国工商银行无锡分行"));
	}
	
	@Test
	public void testSearchWithCSV() {
		File file = new File("src/test/resources/test.csv");
		
		String content = readFile(file);
		System.out.println(content);
		IDKeyword[] keywords = recog.search(content);
		assertEquals(3, keywords.length);
		for(IDKeyword keyword : keywords) {
			if ("mobile".equals(keyword.attr)) {
				assertEquals("13916082774", keyword.word);
			} else if ("email".equals(keyword.attr)) {
				assertEquals("velax_wu@sohu.com", keyword.word);
			} else if ("ssn".equals(keyword.attr)) {
				assertEquals("360430198405111111", keyword.word);
			} else {
				assertTrue(false);
			}
		}
		
	}
	
	@Test
	public void testSearchWithTxt() {
		File file = new File("src/test/resources/test.txt");
		
		String content = readFile(file);
		
		IDKeyword[] keywords = recog.search(content);
		for(IDKeyword keyword: keywords) {
			System.out.println(keyword.attr + keyword.word);
		}
		assertEquals(2, keywords.length);
		for(IDKeyword keyword : keywords) {
			if ("busrid".equals(keyword.attr)) {
				assertEquals("320282000190134", keyword.word);
			} else if ("org".equals(keyword.attr)) {
				assertEquals("无锡市天茂胶辊有限公司", keyword.word);
			} else {
				assertTrue(false);
			}
		}
	}

}
