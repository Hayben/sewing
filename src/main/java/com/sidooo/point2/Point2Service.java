package com.sidooo.point2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidooo.ai.Keyword;

@Service("point2Service")
public class Point2Service {
	
	@Autowired
	private Point2Repository repo;

	public Graph getGraph(String word) {
		
		Graph graph = new Graph();
		
		//一度
		List<Point2> points = repo.getPoints(word);
		System.out.println("Point Count:" + points.size());
		for(Point2 point : points) {
			graph.addNode(new PointNode(point));
			graph.addEdge(new Edge(point.getDocId(), word));
			
			List<Keyword> keywords = repo.getKeywords(point.getDocId());
			System.out.println("Keyword Count:" + keywords.size());
			for(Keyword keyword: keywords) {
				graph.addNode(new KeywordNode(keyword));
				graph.addEdge(new Edge(point.getDocId(), keyword.getWord()));
			}

		}
		
		//二度
		Edge[] edges = graph.edgeIterator();
		for(Edge edge : edges){
			if (edge.to().equals(word)) {
				continue;
			}
			points = repo.getPoints(edge.to());
			for(Point2 point : points) {
				graph.addNode(new PointNode(point));
				graph.addEdge(new Edge(point.getDocId(), edge.to()));
				
				List<Keyword> keywords = repo.getKeywords(point.getDocId());
				
				for(Keyword keyword : keywords) {
					graph.addNode(new KeywordNode(keyword));
					graph.addEdge(new Edge(point.getDocId(), keyword.getWord()));
				}
			}
		}
		
		return graph;
		
	}
	
	public void addPoint(Point2 point, List<Keyword> keywords) throws IOException {
		for(Keyword keyword : keywords) {
			repo.addKeyword(keyword, point);
		}
	}
}
