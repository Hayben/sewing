package com.sidooo.point2;

import com.sidooo.ai.Keyword;

public class KeywordNode extends Node{

	private String attr;
	
	public KeywordNode(Keyword keyword) {
		this.id = keyword.getWord();
		this.title = keyword.getWord();
		this.type = "link";
		this.attr = keyword.getAttr();
	}
}
