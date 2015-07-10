package com.sidooo.point2;

public class Edge {

	private String from;
	private String to;
	
	public Edge(String from , String to) {
		this.from = from;
		this.to = to;
	}
	
	public String from() {
		return this.from;
	}
	
	public String to() {
		return this.to;
	}
	
}
