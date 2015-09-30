package com.sidooo.point2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.sidooo.point.Link;
import com.sidooo.point.LinkComparator;
import com.sidooo.point.Point;

public class Graph {
	
	private Set<Node> nodes = new TreeSet<>(new NodeComparator());
	private Set<Edge> edges = new TreeSet<>(new EdgeComparator());
	
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
	
	public Node[] getNodes() {
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Edge[] getEdges() {
		return edges.toArray(new Edge[edges.size()]);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Node node : nodes) {
			builder.append(node.getLabel() + ",");	
		}
		builder.append("\n");

		for (Edge edge: edges) {
			builder.append(edge.getFrom() + "----" + edge.getTo() + "\n");
		}
		
		return builder.toString();
		
	}

	public boolean existNode(String docId) {
		for (Node node : nodes) {
			if (node.getId().equals(docId)) {
				return true;
			}
		}
		
		return false;
	}

}
