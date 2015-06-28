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

import com.sidooo.senode.DatawareConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestPointService {

	@Autowired
	private PointService pointService;
	
	@Before
	public void setUp() throws Exception {
		pointService.clearPoints();
		pointService.clearLinks();
		pointService.clearItems();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() throws Exception {
		
		NetworkStatus status = pointService.getStatus();
		assertEquals(status.itemCount, 0);
		assertEquals(status.pointCount, 0);
		assertEquals(status.linkCount, 0);
		
		Item itemA = new Item();
		itemA.setContent("无锡斯特精密机械有限公司  320201000153212 13912851136 UC1205");
		itemA.setUrl("http://f.wuxi.gov.cn/32511.html");
		itemA.setTitle("无锡市南长区十佳制造企业");
		pointService.addItem(itemA);
		
		status = pointService.getStatus();
		assertEquals(status.itemCount, 1);
		assertEquals(status.pointCount, 1);
		assertEquals(status.linkCount, 3);
		
		Item itemB = new Item();
		itemB.setContent("无锡荣乐轴承有限公司 320223000001598 UC1205 15801385211 13912851136");
		itemB.setUrl("http://xf.chinawuxi.gov.cn/zgqy/201405.html");
		itemB.setTitle("消防安全隐患整改企业");
		pointService.addItem(itemB);
		
		status = pointService.getStatus();
		assertEquals(status.itemCount, 2);
		assertEquals(status.pointCount, 2);
		assertEquals(status.linkCount, 6);
		
		Item itemC = new Item();
		itemC.setContent("无锡巨港机械有限公司 13089281102 ");
		itemC.setUrl("http://wuxi.114.com/xinqu/5134522.html");
		itemC.setTitle("出口排名前100轴承企业");
		pointService.addItem(itemC);
		
		status = pointService.getStatus();
		assertEquals(status.itemCount, 3);
		assertEquals(status.pointCount, 3);
		assertEquals(status.linkCount, 8);
		
		
		Network network = pointService.search("13912851136", 4);
		Link[] links = network.getLinks();
		Point[] points = network.getPoints();
		assertEquals(links.length, 6);
		assertEquals(points.length, 2);
		
	}

}
