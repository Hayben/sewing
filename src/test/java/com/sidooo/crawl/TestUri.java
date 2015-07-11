package com.sidooo.crawl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUri {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws URISyntaxException {
		URI uri = new URI("http://zengzhaozheng.blog.51cto.com/8219051/1379271");
		assertEquals(uri.getHost(), "zengzhaozheng.blog.51cto.com");
	}

}
