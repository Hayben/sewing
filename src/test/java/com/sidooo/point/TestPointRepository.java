package com.sidooo.point;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sidooo.ai.Keyword;
import com.sidooo.senode.DatawareConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestPointRepository {

	@Autowired
	private PointRepository pointRepo;
	
	@Before
	public void setUp() throws Exception {
		pointRepo.clear();
	}

	@After
	public void tearDown() throws Exception {
		pointRepo.clear();
	}
	

	
	private Point mockPoint() {
		Point point = new Point();
		point.setDocId("fef3451c");
		point.setTitle("工商注册记录");
		point.addLink(new Keyword("13914385123", "mobile"));
		point.addLink(new Keyword("张时安", "nr"));
		return point;
	}
	
	@Test
	public void testPoint() {
		Point mock = mockPoint();
		
		pointRepo.createPoint(mock);
		
		Point point = pointRepo.getPoint(mock.getDocId());
		assertEquals(point.getDocId(), mock.getDocId());
		assertEquals(point.getTitle(), mock.getTitle());
		assertEquals(point.getLinks().length, mock.getLinks().length);
	}
	
	@Test
	public void testUpdatePoint() {
		Point mock = mockPoint();
		pointRepo.createPoint(mock);
		
		mock.setTitle("行政处罚记录");
		mock.addLink(new Keyword("32858159383221", "ssn"));
		pointRepo.updatePoint(mock);
		
		Point point = pointRepo.getPoint(mock.getDocId());
		assertEquals(point.getTitle(), mock.getTitle());
		assertEquals(point.getLinks().length, mock.getLinks().length);
	}
	
	

}
