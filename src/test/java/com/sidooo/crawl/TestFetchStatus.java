package com.sidooo.crawl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFetchStatus {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		List<FetchResult> list = new ArrayList<FetchResult>();
		
		FetchResult s1 = new FetchResult();
		s1.setFetchTime(2222);
		list.add(s1);
		
		FetchResult s2 = new FetchResult();
		s2.setFetchTime(1111);
		list.add(s2);
		
		FetchResult s3 = new FetchResult();
		s3.setFetchTime(1555);
		list.add(s3);
		
		assertEquals(list.get(0).getFetchTime(), 2222);
		assertEquals(list.get(1).getFetchTime(), 1111);
		assertEquals(list.get(2).getFetchTime(), 1555);
		
		Collections.sort(list);
		
		assertEquals(list.get(0).getFetchTime(), 1111);
		assertEquals(list.get(1).getFetchTime(), 1555);
		assertEquals(list.get(2).getFetchTime(), 2222);
	}

}
