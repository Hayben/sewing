package com.sidooo.queue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sidooo.seed.SeedUrl;
import com.sidooo.senode.DatawareConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestQueueRepository {

	@Autowired
	private QueueRepository queue;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		queue.sendSeedUrl("abcdefg", "http://www.baidu.com/");
		SeedUrl request = queue.receiveSeedUrl();
		assertEquals(request.seedId, "abcdefg");
		assertEquals(request.url, "http://www.baidu.com/");
	}

}
