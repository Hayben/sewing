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

import com.sidooo.wheart.DatawareConfiguration;

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
	
	private Link mockLink() {
		Link link = new Link();
		link.setKeyword("13914385123");
		link.setType("mobile");
		link.addPoint("abcdefg");
		return link;
	}
	
	private Point mockPoint() {
		Point point = new Point();
		point.setDocumentId("fef3451c");
		point.setTitle("工商注册记录");
		point.addLink("13914385123");
		point.addLink("张时安");
		return point;
	}

	@Test
	public void testLink() {

		Link mock = mockLink();
		
		pointRepo.createLink(mock);
		
		Link link = pointRepo.getLink(mock.getKeyword());
		assertEquals(link.getKeyword(), mock.getKeyword());
		assertEquals(link.getType(), mock.getType());
		assertEquals(link.getPointList().size(), mock.getPointList().size());
	}
	
	@Test
	public void testPoint() {
		Point mock = mockPoint();
		
		pointRepo.createPoint(mock);
		
		Point point = pointRepo.getPoint(mock.getDocumentId());
		assertEquals(point.getDocumentId(), mock.getDocumentId());
		assertEquals(point.getTitle(), mock.getTitle());
		assertEquals(point.getLinks().size(), mock.getLinks().size());
	}
	
	@Test
	public void testSearch() throws Exception {
		Point pointA = new Point();
		pointA.setDocumentId("A");
		pointA.setTitle("无锡市南长区十佳制造企业");
		pointA.addLink("无锡斯特精密机械有限公司");
		pointA.addLink("320201000153212");
		pointA.addLink("UC1205");
		pointA.addLink("13912851136");
		pointRepo.createPoint(pointA);
		
		Point pointB = new Point();
		pointB.setDocumentId("B");
		pointB.setTitle("消防安全隐患整改企业");
		pointB.addLink("无锡荣乐轴承有限公司");
		pointB.addLink("320223000001598");
		pointB.addLink("UC1205");
		pointRepo.createPoint(pointB);
		
		Point pointC = new Point();
		pointC.setDocumentId("C");
		pointC.setTitle("出口排名前100轴承企业");
		pointC.addLink("无锡巨浪机械有限公司");
		pointC.addLink("13089281102");
		pointRepo.createPoint(pointC);
		
		Link link0 = new Link();
		link0.setKeyword("无锡斯特精密机械有限公司");
		link0.setType("org");
		link0.addPoint("A");
		pointRepo.createLink(link0);
		
		Link link1 = new Link();
		link1.setKeyword("13912851136");
		link1.setType("mobile");
		link1.addPoint("A");
		pointRepo.createLink(link1);
		
		Link link2 = new Link();
		link2.setKeyword("UC1205");
		link2.setType("model");
		link2.addPoint("A");
		link2.addPoint("B");
		pointRepo.createLink(link2);
		
		Link link3 = new Link();
		link3.setKeyword("320201000153212");
		link3.setType("busrid");
		link3.addPoint("A");
		pointRepo.createLink(link3);
		
		Link link4 = new Link();
		link4.setKeyword("320223000001598");
		link4.setType("busrid");
		link4.addPoint("B");
		pointRepo.createLink(link4);
		
		Link link5 = new Link();
		link5.setKeyword("无锡荣乐轴承有限公司");
		link5.setType("org");
		link5.addPoint("B");
		pointRepo.createLink(link5);
		
		Link link6 = new Link();
		link6.setKeyword("无锡巨浪机械有限公司");
		link6.setType("org");
		link6.addPoint("C");
		pointRepo.createLink(link6);

		Link link7 = new Link();
		link7.setKeyword("13089281102");
		link7.setType("mobile");
		link7.addPoint("C");
		pointRepo.createLink(link7);
		
		Network network = pointRepo.search("UC1205", 4);
		Link[] links = network.getLinks();
		Point[] points = network.getPoints();
		assertEquals(links.length, 6);
		assertEquals(points.length, 2);
		
	}

}
