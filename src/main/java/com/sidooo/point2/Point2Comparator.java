package com.sidooo.point2;

import java.util.Comparator;

public class Point2Comparator implements Comparator<Point2>{
	
	public int compare(Point2 o1, Point2 o2) {
		String id1 = o1.getDocId();
		String id2 = o2.getDocId();
		return id1.compareTo(id2);
	}
}
