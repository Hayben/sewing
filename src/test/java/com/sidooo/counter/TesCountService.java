package com.sidooo.counter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import com.sidooo.senode.RedisConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RedisConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TesCountService {

	@Autowired
	private CountService countService;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		
		
		String mockSeedId = "test";
		
		countService.resetCount(mockSeedId);
		
		countService.incPointCount(mockSeedId);
		
		countService.incLinkCount(mockSeedId);
		countService.incLinkCount(mockSeedId);
		countService.incLinkCount(mockSeedId);
		
		assertEquals(1, countService.getPointCount(mockSeedId));
		assertEquals(3, countService.getLinkCount(mockSeedId));
		
		
	}

}
