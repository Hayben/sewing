package com.sidooo.fetcher;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.crawl.Filter;

public class TestFilter {

	private Filter filter = new Filter();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(filter.accept("http://www.sidooo.com/"));
		assertTrue(filter.accept("http://www.sidooo.com/a.htm"));
		assertTrue(filter.accept("http://www.sidooo.com/a.pdf"));
		assertTrue(filter.accept("http://www.sidooo.com/a.xls"));
		assertTrue(filter.accept("http://www.sidooo.com/a.csv"));
		assertFalse(filter.accept("http://www.sidooo.com/a.txt"));
		assertTrue(filter.accept("http://www.sidooo.com/a"));
		assertFalse(filter.accept("http://www.sidooo.com/a.js"));
	}

}
