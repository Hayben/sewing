package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestInc {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		Context context = new Context();
		context.data.setVariable("page", "1");
		
		Inc inc = new Inc("page");
		inc.execute(context);
		
		assertEquals(context.data.getVariable("page"), "2");
	}

}
