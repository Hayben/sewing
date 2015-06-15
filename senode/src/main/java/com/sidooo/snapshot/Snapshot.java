package com.sidooo.snapshot;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "snapshot")
public class Snapshot {
	
	@Id
	@Field("url")
	public String originUrl;
	
	@Field("snap")
	public String snapUrl;
	
	@Field("size")
	public long 	size;
	
	@Field("date")
	public Date 	date;
}
