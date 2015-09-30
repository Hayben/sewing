package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestVar {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Context context = new Context();
		Var var = new Var("var", "1");
		var.execute(context);
		String value = context.data.getVariable("var");
		assertEquals(value, "1");
	}
	
	@Test
	public void test1() {
		Context context = new Context();
		String[] content = {"queryInfor('/ecipplatform/inner_ci/ci_queryCorpInfor_gsRelease.jsp','1022','23200210','3','320282000190134','ecipplatform')"};
		context.data.setContent(content);
		Var var = new Var("org", SelectMethod.SPLIT, ",", new Integer(1), "trim");
		var.execute(context);
		assertEquals(context.data.getVariable("org"), "1022");
	}

}
