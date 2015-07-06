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
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(500);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(404);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(404);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test2() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchStatus();
			status.setStatus(404);
			fetches.add(status);
		}

		assertEquals(UrlStatus.from(fetches), UrlStatus.UNREACHABLE);
	}
	
	@Test
	public void test3() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test4() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(199);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.FILTERED);
	}
	
	@Test
	public void test5() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchStatus();
			status.setStatus(404);
			fetches.add(status);
		}
		
		status = new FetchStatus();
		status.setStatus(200);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test6() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchStatus();
			status.setStatus(404);
			fetches.add(status);
		}
		
		status = new FetchStatus();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(404);
		fetches.add(status);

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test7() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(200);
		fetches.add(status);
		
		for(int i=0; i<UrlStatus.RETRY_LIMIT - 1; i++) {
			status = new FetchStatus();
			status.setStatus(404);
			fetches.add(status);
		}

		assertEquals(UrlStatus.from(fetches), UrlStatus.LATEST);
	}
	
	@Test
	public void test8() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		status = new FetchStatus();
		status.setStatus(0);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(200);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setStatus(199);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.FILTERED);
	}
	
	@Test
	public void test9() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD;
		
		status = new FetchStatus();
		status.setFetchTime(stamp-1);
		status.setStatus(1);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setFetchTime(stamp);
		status.setStatus(200);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test10() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD;
		status = new FetchStatus();
		status.setFetchTime(stamp-1);
		status.setStatus(1);
		fetches.add(status);
		
		status = new FetchStatus();
		status.setFetchTime(stamp);
		status.setStatus(199);
		fetches.add(status);
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}
	
	@Test
	public void test11() {
		List<FetchStatus> fetches = new ArrayList<FetchStatus>();
		
		FetchStatus status = new FetchStatus();
		
		long stamp = System.currentTimeMillis() - UrlStatus.PERIOD -100;
		status = new FetchStatus();

		
		for(int i=0; i<UrlStatus.RETRY_LIMIT; i++) {
			status = new FetchStatus();
			status.setFetchTime(stamp+i);
			status.setStatus(404);
			fetches.add(status);
		}
		
		assertEquals(UrlStatus.from(fetches), UrlStatus.READY);
	}

}
