package com.sidooo.point;

import java.util.Set;
import java.util.TreeSet;

public class Network {

	private Set<Link> links = new TreeSet<>(new LinkComparator());
	private Set<Point> points = new TreeSet<>(new PointComparator());
	
	public void addLink(Link link) {
		links.add(link);
	}
	
	public void addPoint(Point point) {
		points.add(point);
	}
	
	public Link[] getLinks() {
		return links.toArray(new Link[links.size()]);
	}
	
	public Point[] getPoints() {
		return points.toArray(new Point[points.size()]);
	}
}
