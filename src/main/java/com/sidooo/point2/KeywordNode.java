package com.sidooo.point2;

import com.sidooo.ai.Keyword;

public class KeywordNode extends Node{

	private String attr;
	
	public KeywordNode(Keyword keyword) {
		setType("link");
		setId(keyword.getWord());
		setLabel(keyword.getWord());
		
		this.attr = keyword.getAttr();
	}
	
	public KeywordNode(String companyName) {
		setType("link");
		setId(companyName);
		setLabel(companyName);
		
		this.attr = "nt";
	}

	public String getAttr() {
		return this.attr;
	}
}
