package com.sidooo.content;

import java.util.ArrayList;
import java.util.List;

public class WebList {
	
	private List<String> items = new ArrayList<String>();
	
	public int size() {
		return items.size();
	}

	public String get(int i) {
		if (i >= items.size() || i < 0) {
			return null;
		}
		return items.get(i);
	}
	
	public static WebList from(HttpContent http) {
		if (http == null) {
			return null;
		}
		
		WebList list = new WebList();
		
		return list;
	}


}
