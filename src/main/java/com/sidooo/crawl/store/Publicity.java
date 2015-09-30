package com.sidooo.crawl.store;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sidooo.saic.SaicInfo;
import com.sidooo.saic.SaicPublisher;

@Document(collection="corpration")
public class Publicity {

	@Id
	@Field("id")
	public String company; 
	
	@Field("bureau")
	public Corpration bureau;
	
	@Field("corpration")
	public Corpration corpration;
	
	@Field("justice")
	public Corpration justice;
	
	@Field("other")
	public Corpration other;
	

	
}
