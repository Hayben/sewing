package com.sidooo.snapshot;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sidooo.senode.DatawareConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestSnapshotRepository {

	@Autowired
	private SnapshotRepository snapRepo;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		
		URL url = new URL("file://archive/data/court/index-1000.htm");
		snapRepo.deleteSnapShot(url);
		
		SnapFile snapFile = snapRepo.createSnapFile(url);
		assertTrue(snapFile != null);
		assertEquals(snapFile.getPath(), "smb://archive.sidooo.com/test/data/court/index-1000.htm");
		
		OutputStream out = new SnapFileOutputStream(snapFile);
		out.write("test".getBytes());
		out.close();
		
		assertEquals(snapFile.length(), 4);
		
		snapRepo.createSnapshot(url, snapFile);
		
		assertTrue(snapRepo.existSnapshot(url));
		
	}
	
}
