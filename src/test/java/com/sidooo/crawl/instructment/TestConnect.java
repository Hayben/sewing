package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConnect {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		Context context = new Context();
		context.data.setVariable("host", "http://www.jsgsj.gov.cn:58888");
		
		Connect connect = new Connect("/province");
		connect.execute(context);
		
		String[] response = context.data.getContent();
		assertTrue(response.length == 1);
//		for(String item : response) {
//			System.out.println(item);
//		}
	}

}
