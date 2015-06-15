package com.sidooo.item;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sidooo.wheart.DatawareConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestItemRepository {

	@Autowired
	private ItemRepository itemRepo;
	
	@Before
	public void setUp() throws Exception {
		itemRepo.clear();
	}
	
	@After
	public void tearDown() throws Exception {
		itemRepo.clear();
	}
	
	private Item mockItem() throws Exception {
		Item item = new Item();
		item.title = "test";
		item.originUrl = "http://test/a.txt";
		item.snapUrl = "smb://archive/test/a.txt";
		item.seed =  "mockseed";
		item.content = "abcdefg";
		return item;
	}
	
	@Test 
	public void testDuplicationItem() throws Exception {
		
		Item mock = mockItem();
		String id1 = itemRepo.saveItem(mock);
		String id2 = itemRepo.saveItem(mock);
		
		assertEquals(id1, id2);
		
		long count = itemRepo.getItemCount();	
		assertEquals(count, 1);
		
		Item item = itemRepo.getItem(id1);
		assertEquals(item.title, mock.title);
		assertEquals(item.originUrl, mock.originUrl);
		assertEquals(item.snapUrl, mock.snapUrl);
		assertEquals(item.seed, mock.seed);
		assertEquals(item.content, mock.content);
	}
}
