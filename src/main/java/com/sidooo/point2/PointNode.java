package com.sidooo.point2;

public class PointNode extends Node{
	

	public String url;

	public PointNode(Point2 point) {
		this.id = point.getDocId();
		this.type = "point";
		this.title = point.getTitle();
		this.url = point.getUrl();
	}
}
