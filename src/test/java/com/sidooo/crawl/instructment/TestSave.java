package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.saic.SaicInfo;
import com.sidooo.saic.SaicPublisher;

public class TestSave {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String[] content = {"test1", "test2"};
		Context context = new Context();
		context.data.setContent(content);
		Save save = new Save(SaicPublisher.BUREAU, SaicInfo.BRANCH);
		save.execute(context);
		
	}

}
