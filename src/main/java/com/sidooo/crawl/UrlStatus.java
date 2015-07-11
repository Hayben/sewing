package com.sidooo.crawl;

import org.hsqldb.lib.Iterator;

public enum UrlStatus {
	READY(0),
	LATEST(1),
	FILTERED(2),
	UNREACHABLE(3);
	
	private int value = 0;
	
	private UrlStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static UrlStatus valueOf(int value) {
		switch(value) {
		case 0:
			return UrlStatus.READY;
		case 1:
			return UrlStatus.LATEST;
		case 2:
			return UrlStatus.FILTERED;
		case 3:
			return UrlStatus.UNREACHABLE;
		default:
			return null;
		}
	}
	

	public static final long PERIOD = 15 * 24 * 60 * 60 * 1000;
	public static final int  RETRY_LIMIT = 3;
	
	public static UrlStatus from(Iterable<FetchResult> fetches) {

		int retryCount = 0;
		for(FetchResult it : fetches) {
			int response = it.getStatus();
			
			if (response == 0 || response == 1)
				continue;
			
			if ((System.currentTimeMillis() - it.getFetchTime()) >= PERIOD) {
				//超过期限的记录全部放弃
				break;
			}
			
			if (response == 199) {
				//文件过大
				return FILTERED;
			}
			
			if (response == 200) {
				return LATEST;
			}
			
			retryCount ++;
			if (retryCount >= RETRY_LIMIT) {
				return UNREACHABLE;
			}
		}
		
		return READY;
	}

}
