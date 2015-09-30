package com.sidooo.point2;

import java.util.Comparator;

import com.sidooo.ai.Keyword;

public class KeywordComparator implements Comparator<Keyword> {

	@Override
	public int compare(Keyword o1, Keyword o2) {
		String k1 = o1.getWord();
		String k2 = o2.getWord();
		return k1.compareTo(k2);
	}
}
