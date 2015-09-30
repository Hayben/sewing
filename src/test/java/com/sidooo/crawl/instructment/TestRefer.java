package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRefer {

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
		
		Refer refer = new Refer("/test/refer");
		refer.execute(context);
		
		assertEquals(context.fetcher.getRefer(), "http://www.jsgsj.gov.cn:58888/test/refer");
		System.out.println(context.fetcher.getRefer());
	}

}
