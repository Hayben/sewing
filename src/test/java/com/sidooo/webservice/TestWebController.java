package com.sidooo.webservice;

import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.sidooo.config.AppConfig;
import com.sidooo.config.core.SpringMvcInitializer;
import com.sidooo.seed.Seed;
import com.sidooo.sewing.WebController;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes=AppConfig.class, loader=AnnotationConfigContextLoader.class)
//@WebAppConfiguration
public class TestWebController {

//	@Autowired
//	private WebApplicationContext applicationContext;
//	
//	@Autowired
//	private WebController webctrl;
//	
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//	
//	private Seed mockSeed() {
//		Seed seed = new Seed();
//		seed.setDivision(0);
//		seed.setEnabled(false);
//		seed.setLevel("A");
//		seed.setName("Test");
//		seed.setReliability("GOV");
//		seed.setType("smb");
//		seed.setUrl("smb://test/data/");
//		return seed;
//	}
//
//	@Test
//	public void test() {
//		List<Seed> seeds = webctrl.getSeedList();
//		assertEquals(seeds.size(), 0);
//		
//		Seed mock = mockSeed();
//		webctrl.createSeed(mock);
//		
//		seeds = webctrl.getSeedList();
//		assertEquals(seeds.size(), 1);
//	}

}
