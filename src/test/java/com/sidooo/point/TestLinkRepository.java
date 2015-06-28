package com.sidooo.point;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sidooo.senode.DatawareConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestLinkRepository {

	@Autowired
	private LinkRepository linkRepo;
	
	@Before
	public void setUp() throws Exception {
		linkRepo.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	private Link mockLink() {
		Link link = new Link();
		link.setKeyword("13914385123");
		link.setType("mobile");
		link.addPoint("abcdefg");
		return link;
	}
	
	@Test
	public void testLink() {

		Link mock = mockLink();
		
		linkRepo.createLink(mock);
		
		Link link = linkRepo.getLink(mock.getKeyword());
		assertEquals(link.getKeyword(), mock.getKeyword());
		assertEquals(link.getType(), mock.getType());
		assertEquals(link.getPointList().length, mock.getPointList().length);
	}
	
	@Test
	public void testUpdateLink() {
		Link mock = mockLink();
		linkRepo.createLink(mock);
		
		mock.setType("ssn");
		mock.addPoint("hijklmn");
		linkRepo.updateLink(mock);
		
		Link link = linkRepo.getLink(mock.getKeyword());
		
		assertEquals(link.getKeyword(), mock.getKeyword());
		assertEquals(link.getType(), mock.getType());
		String[] pointIds = link.getPointList();
		assertEquals(pointIds[0], "abcdefg");
		assertEquals(pointIds[1], "hijklmn");
	}
	

}
