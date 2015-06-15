package com.sidooo.point;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

	public int compare(Point o1, Point o2) {
		String id1 = o1.getDocumentId();
		String id2 = o2.getDocumentId();
		return id1.compareTo(id2);
	}

}