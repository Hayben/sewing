package com.sidooo.fetcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestSambaFetcher {

	private File localFile;
	
	@Before
	public void setUp() throws Exception {
		jcifs.Config.registerSmbURLHandler();
	}

	@After
	public void tearDown() throws Exception {
		if (localFile != null) {
			localFile.delete();
		}
	}

	@Test
	public void test() throws Exception {
//		URL url = new URL("smb://archive.sidooo.com/book/泥鸽靶.pdf");
//		localFile = new File("泥鸽靶.pdf");
//		OutputStream out = new FileOutputStream(localFile);
//		SambaFetcher fetcher = new SambaFetcher(url, out);
//		fetcher.setAccount("kimzhang", "Libra8968");
//		fetcher.fetch();
//		out.close();
//		
//		assertTrue(localFile.exists());
//		assertEquals(localFile.length(), 4364845);
	}

}
