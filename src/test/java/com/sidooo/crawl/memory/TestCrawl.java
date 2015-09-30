package com.sidooo.crawl.memory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;   

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.crawl.instructment.ConfigurationManager;
import com.sidooo.crawl.instructment.Crawl;
import com.sidooo.crawl.interrupt.Interrupt;
import com.sidooo.crawl.store.CorprationRepository;

public class TestCrawl {
	
	private ConfigurationManager confmgr;

	@Before
	public void setUp() throws Exception {
		
		confmgr = ConfigurationManager.newInstance("saic.xml");
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testJiangsu() throws Exception {
		
		CorprationRepository repo = mock(CorprationRepository.class);
		
		String conf = confmgr.getConf("江苏省");
		assertTrue(conf != null);
		Crawl crawl = new Crawl(conf, repo);
		Interrupt interrupt = crawl.run();
		assertEquals(interrupt.getCode(), 1);
		assertTrue(interrupt.getMessage() != null);
		System.out.println(interrupt.getMessage());
	}

}
