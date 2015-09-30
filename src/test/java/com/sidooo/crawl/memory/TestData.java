package com.sidooo.crawl.memory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestData {

	private Data data;
	
	@Before
	public void setUp() throws Exception {
		data = new Data();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		
		data.setVariable("abc", "123");
		assertEquals(data.getVariable("abc"), "123");
	}
	
	

}
