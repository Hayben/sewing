package com.sidooo.point2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sidooo.point.Link;
import com.sidooo.point.Point;

public class Graph {
	
	Set<Node> nodes = new HashSet<Node>();
	Set<Edge> edges = new HashSet<Edge>();
	
	public void addNode(Node node) {
		this.nodes.add(node);
	}
	
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	

	
	public int getNodeCount() {
		return nodes.size();
	}
	
	public int getEdgeCount() {
		return edges.size();
	}
	
	public Node[] nodeIterator() {
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Edge[] edgeIterator() {
		return edges.toArray(new Edge[edges.size()]);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Node node : nodes) {
			builder.append(node.title + ",");	
		}
		builder.append("\n");
		
		return builder.toString();
		
	}

}
