package com.sidooo.point2;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge>{

	@Override
	public int compare(Edge o1, Edge o2) {
		String from1 = o1.getFrom();
		String to1 = o1.getTo();
		String from2 = o2.getFrom();
		String to2 = o2.getTo();
		
		if (from1.equalsIgnoreCase(from2) && to1.equalsIgnoreCase(to2)) {
			return 0;
		}
		
		if (from1.equalsIgnoreCase(to2) && from2.equalsIgnoreCase(to1)) {
			return 0;
		}
		
		int ret = from1.compareTo(from2);
		if (ret != 0) {
			return ret;
		} else {
			return to1.compareTo(to2);
		}
		
	}

}
