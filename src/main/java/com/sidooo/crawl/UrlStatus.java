package com.sidooo.crawl;

public enum UrlStatus {
	READY(0),
	LATEST(1),
	FILTERED(2),
	UNREACHABLE(2);
	
	private int value = 0;
	
	private UrlStatus(int value) {
		this.value = value;
	}
	
	public static final long PERIOD = 15 * 24 * 60 * 60 * 1000;
	public static final int  RETRY_LIMIT = 15;
	
	public static UrlStatus from(Iterable<FetchStatus> fetches) {

		long  lastSuccessTime = 0;
		long lastFetchTime = 0;
		boolean hasSuccess = false;
		boolean hasFiltered = false;
		int retryCount = 0;
		for(FetchStatus it : fetches) {
			int response = it.getStatus();
			switch(response) {

			case 0:
				break;
			case 1:
				break;
			case 199:
				hasFiltered = true;
				hasSuccess = false;
				retryCount = 0;
				break;
			case 200:
				hasSuccess = true;
				lastSuccessTime = it.getFetchTime();
				hasFiltered = false;
				retryCount = 0;
				break;
			default: 
				retryCount ++;
				break;
			}
			
			if (it.getFetchTime() > lastFetchTime) {
				lastFetchTime = it.getFetchTime();
			}
		}
		
		if ((System.currentTimeMillis() - lastFetchTime) >= PERIOD) {
			//任何URL超过时间期限， 就重新尝试获取
			return READY;
		}
		
		if (retryCount >= RETRY_LIMIT) {
			//URL超过retry上限, 就放弃
			return UNREACHABLE;
		}

		if (hasSuccess) {
			if ((System.currentTimeMillis() - lastSuccessTime) < PERIOD) {
				return LATEST;
			}
		} 
		
		if (hasFiltered) {
			return FILTERED;
		}
		
		return READY;
	}

}
