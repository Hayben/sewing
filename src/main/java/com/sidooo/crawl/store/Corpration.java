package com.sidooo.crawl.store;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


public class Corpration {

	//注册登记信息
	public List<String> registration;
	
	//备案信息
	public List<String> filing;
	
	//抵押信息
	public List<String> mortgage;
	
	//行政许可
	public List<String> permission; 
	
	//行政处罚
	public List<String> penalty;
	
	//经营异常
	public List<String> abnormity;
	
	//违法记录
	public List<String> illegal;
	
	//质量抽检
	public List<String> inspection;
	
	//企业年报
	public List<String> annual;
	
	//投资人
	public List<String> investor;
	
	//清算
	public List<String> liquidation;
	
	//股权冻结/变更
	public List<String> equity;
	
	//知识产权
	public List<String> patent;	
}
