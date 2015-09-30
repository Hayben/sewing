package com.sidooo.crawl.memory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.sidooo.crawl.instructment.BaseInstructment;
import com.sidooo.crawl.instructment.ConfigurationManager;
import com.sidooo.crawl.instructment.Connect;
import com.sidooo.crawl.instructment.CrawlConfiguration;
import com.sidooo.crawl.instructment.Inc;
import com.sidooo.crawl.instructment.JumpWhenSuccess;
import com.sidooo.crawl.instructment.Refer;
import com.sidooo.crawl.instructment.Save;
import com.sidooo.crawl.instructment.SelectMethod;
import com.sidooo.crawl.instructment.Submit;
import com.sidooo.crawl.instructment.Var;
import com.sidooo.saic.SaicInfo;
import com.sidooo.saic.SaicPublisher;

public class TestCode {

	private Code code;
	
	private ConfigurationManager manager;
	
	@Before
	public void setUp() throws Exception {
		code = new Code();
		manager = ConfigurationManager.newInstance("testconf.xml");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception{
		
		assertTrue(manager != null);
		
		assertTrue(manager.getConf("test") != null);
		
		code.compile(manager.getConf("test"));
		
		BaseInstructment ins = code.getInstuctment(0);
		assertTrue(ins instanceof Var);
		Var insVar = (Var)ins;
		assertEquals(insVar.getName(), "name");
		assertEquals(insVar.getValue(), "test");
		assertEquals(insVar.getMethod(), null);
		assertEquals(insVar.getKey(), null);
		assertEquals(insVar.getIndex(), null);
		
		ins = code.getInstuctment(1);
		insVar = (Var)ins;
		assertEquals(insVar.getName(), "host");
		assertEquals(insVar.getValue(), "http://www.test.com/");
		assertEquals(insVar.getMethod(), null);
		assertEquals(insVar.getKey(), null);
		assertEquals(insVar.getIndex(), null);
		
		ins = code.getInstuctment(2);
		Connect insConnect = (Connect)ins;
		assertEquals(insConnect.getPath(), "province");
		
		ins = code.getInstuctment(3);
		insVar = (Var)ins;
		assertEquals(insVar.getName(), "id");
		assertEquals(insVar.getValue(), null);
		assertEquals(insVar.getMethod(), SelectMethod.SPLIT);
		assertEquals(insVar.getKey(), ",");
		assertEquals(insVar.getIndex(), new Integer(3));
		
		ins = code.getInstuctment(4);
		Refer insRefer  = (Refer)ins;
		assertEquals(insRefer.getRefer(), "refer/test");
		
		ins = code.getInstuctment(5);
		Save insSave = (Save)ins;
		assertEquals(insSave.getFrom(), SaicPublisher.BUREAU);
		assertEquals(insSave.getType(), SaicInfo.REGISTRATION);

		ins = code.getInstuctment(6);
		insVar = (Var)ins;
		assertEquals(insVar.getName(), "page");
		assertEquals(insVar.getValue(), "1");
		assertEquals(insVar.getMethod(), null);
		assertEquals(insVar.getKey(), null);
		assertEquals(insVar.getIndex(), null);
		
		ins = code.getInstuctment(7);
		Submit insSubmit = (Submit)ins;
		assertEquals(insSubmit.getPath(), "test/test.html");
		
		int inputCount = insSubmit.getInputCount();
		assertEquals(inputCount, 1);
		assertEquals(insSubmit.getInputName(0), "pageNo");
		assertEquals(insSubmit.getInputValue(0), "$page");
		
		ins = code.getInstuctment(8);
		Save save = (Save)ins;
		assertEquals(save.getFrom(), SaicPublisher.CORPRATION);
		assertEquals(save.getType(), SaicInfo.PERMISSION);
		
		ins = code.getInstuctment(9);
		Inc insInc = (Inc)ins;
		assertEquals(insInc.getVariable(), "page");
		
		ins = code.getInstuctment(10);
		JumpWhenSuccess insJump = (JumpWhenSuccess)ins;
		assertEquals(insJump.getTargetAddress(), 7);
		
	}

}
