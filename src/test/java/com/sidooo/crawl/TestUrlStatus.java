package com.sidooo.crawl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUrlStatus {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(500);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(404);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(404);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test2() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchResult();
			status.setStatus(404);
			fetches.add(status);
		}

		assertEquals(UrlStatus.from(fetches), UrlStatus.UNREACHABLE);
	}
	
	@Test
	public void test3() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test4() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(199);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.FILTERED);
	}
	
	@Test
	public void test5() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchResult();
			status.setStatus(404);
			fetches.add(status);
		}
		
		status = new FetchResult();
		status.setStatus(200);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test6() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchResult();
			status.setStatus(404);
			fetches.add(status);
		}
		
		status = new FetchResult();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(404);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test7() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(200);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT - 1; i++) {
			status = new FetchResult();
			status.setStatus(404);
			fetches.add(status);
		}

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test8() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		status = new FetchResult();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchResult();
		status.setStatus(199);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.FILTERED);
	}
	
	@Test
	public void test9() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD;
		
		status = new FetchResult();
		status.setFetchTime(stamp-1);
		status.setStatus(1);
		fetches.add(status);
		
		status = new FetchResult();
		status.setFetchTime(stamp);
		status.setStatus(200);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test10() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD;
		status = new FetchResult();
		status.setFetchTime(stamp-1);
		status.setStatus(1);
		fetches.add(status);
		
		status = new FetchResult();
		status.setFetchTime(stamp);
		status.setStatus(199);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test11() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		FetchResult status = new FetchResult();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD -100;
		status = new FetchResult();

		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchResult();
			status.setFetchTime(stamp+i);
			status.setStatus(404);
			fetches.add(status);
		}
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test12() {
		List<FetchResult> fetches = new ArrayList<FetchResult>();
		
		//long time = System.currentTimeMillis() - 24* 60 *60 *1000;
		long time = Long.parseLong("1436455572267");
		for(int i=0; i<100; i++) {
			FetchResult status = new FetchResult();
			status.setFetchTime(time);
			status.setStatus(200);
			fetches.add(status);
		}
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}

}
