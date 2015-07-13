package com.sidooo.point2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
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
import com.sidooo.senode.HtableConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=HtableConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestPoint2Service {

	@Autowired
	private Point2Service pointService; 
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {

		Point2 point = new Point2();
		point.setDocId("abcdef01234");
		point.setTitle("test");
		point.setUrl("http://www.test.com/index.html");
		
		Keyword keyword1 = new Keyword();
		keyword1.setAttr("mobile");
		keyword1.setWord("13812551600");
		
		Keyword keyword2 = new Keyword();
		keyword2.setAttr("nt");
		keyword2.setWord("无锡斯特精密机械有限公司");
		
		List<Keyword> keywords = new ArrayList<Keyword>();
		keywords.add(keyword1);
		keywords.add(keyword2);
		
		pointService.addPoint(point, keywords);
		
		Graph graph = pointService.getGraph("13812551600");
		System.out.println(graph.toString());
		assertEquals(3, graph.getNodeCount());
		assertEquals(2, graph.getEdgeCount());
	}

}
