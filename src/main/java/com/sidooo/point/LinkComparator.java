package com.sidooo.point;

import java.util.Comparator;

public class LinkComparator implements Comparator<Link>{

	public int compare(Link o1, Link o2) {
		String k1 = o1.getKeyword();
		String k2 = o2.getKeyword();
		return k1.compareTo(k2);
	}

}
