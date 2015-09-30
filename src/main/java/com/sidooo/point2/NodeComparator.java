package com.sidooo.point2;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node o1, Node o2) {
		String k1 = o1.getId();
		String k2 = o2.getId();
		return k1.compareTo(k2);
	}

}
