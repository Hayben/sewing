package com.sidooo.point2;

public class PointNode extends Node{
	
	public String url;

	public PointNode(Point2 point) {
		this.setType("point");
		this.setId(point.getDocId());
		this.setLabel(point.getTitle());
		this.url = point.getUrl();
	}
	
	public String getUrl() {
		return this.url;
	}
}
