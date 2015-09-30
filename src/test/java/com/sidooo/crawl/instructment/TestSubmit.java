package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSubmit {

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
		Submit submit = new Submit("/province/queryResultList.jsp");
		submit.addInput("typeName", "无锡市天茂胶辊有限公司");
		submit.addInput("verifyCode", "8tqUt9");
		submit.execute(context);
		
		String[] contents = context.data.getContent();
		assertTrue(contents != null);
		
		for(String content : contents) {
			System.out.println(content);
		}
		
	}

}
