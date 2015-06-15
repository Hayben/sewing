package com.sidooo.formatter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CSVFormatter implements Formatter{
	
	private String getSplit(String headerLine) {
		String[] result = headerLine.split(",");
		if (result.length >= 1) {
			return ",";
		}
		
		result = headerLine.split("\t");
		if (result.length >= 1) {
			return "\t";
		}
		
		return null;
	}

	public String format(String content, String comment) throws Exception{
		
		String splitChar = getSplit(comment); 
		if (splitChar == null) {
			throw new Exception("invalid split char.");
		}
		
		String[] headers = comment.split(splitChar);
		
		String[] tokens = content.split(splitChar);
		
		if (headers.length <= 0 || tokens.length <= 0) {			
			throw new Exception("field count is 0");
		} 
		
		if (headers.length != tokens.length) {
			throw new Exception("field length is not match");
		} 
		
		DBObject doc = new BasicDBObject();
		
		for(int i=0; i<headers.length; i++) {
			doc.put(headers[i], tokens[i]);
		}
		
		return doc.toString();
	}
}
