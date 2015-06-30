package com.sidooo.crawl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UrlStatus {
	
	private final long PERIOD = 10 * 24 * 60 * 1000;
	private final int  RETRY_LIMIT = 15;
	
	List<FetchStatus> status;
	
	public UrlStatus(List<FetchStatus> status) {
		this.status = status;
	}
	
	public UrlStatus(Iterator<FetchStatus> it) {
		status = new ArrayList<FetchStatus>();
		while(it.hasNext()) {
			status.add(it.next());
		}
	}
	
	public boolean hasSizeLimit() {
		for(FetchStatus it : status) {
			if (it.getStatus() == 199) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasRetryLimit() {
		int count = 0;
		for(FetchStatus it : status) {
			int response = it.getStatus();
			if (response != 200 &&
					response != 0 &&
					response != 1 &&
					response != 199 ) {
				count ++;
			}
		}
		
		return count >= RETRY_LIMIT;
	}
	
	public boolean hasExpired() {
		for(FetchStatus it : status) {
			
			if (it.getStatus() == 200 &&
				(System.currentTimeMillis() - it.getFetchTime()) < PERIOD) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasSucceed() {
		
		for (FetchStatus it : status) {
			if (it.getStatus() == 200) {
				return true;
			}
		}
		
		return false;
	}

}
