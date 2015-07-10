package com.sidooo.point2;

import com.sidooo.ai.Keyword;

public class KeywordNode extends Node{

	private String id;
	private String title;
	private String attr;
	
	public KeywordNode(Keyword keyword) {
		this.id = keyword.getWord();
		this.title = keyword.getWord();
		this.attr = keyword.getAttr();
	}
}
