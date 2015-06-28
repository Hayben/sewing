package com.sidooo.seed;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sidooo.senode.DatawareConfiguration;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatawareConfiguration.class, loader=AnnotationConfigContextLoader.class)
public class TestSeedRepository extends TestCase {

	@Autowired
	private SeedRepository seedRepo;
	
	@Before
	public void setUp() throws Exception {
		seedRepo.clear();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	private Seed mockSeed() {
		Seed seed = new Seed();
		seed.setName("Test");
		seed.setEnabled(true);
		seed.setReliability("GOV");
		seed.setDivision(0);
		seed.setLevel("A");
		seed.setUrl("http://test/");
		return seed;
	}
	
	private void compareSeed(Seed seed1, Seed seed2) {
		assertEquals(seed1.getName(), seed2.getName());
		assertEquals(seed1.getId(), seed2.getId());
		assertEquals(seed1.getType(), seed2.getType());
		assertEquals(seed1.getReliability(), seed2.getReliability());
		assertEquals(seed1.getUrl(), seed2.getUrl());
		assertEquals(seed1.getDivision(), seed2.getDivision());
		assertEquals(seed1.getLevel(), seed2.getLevel());
		assertEquals(seed1.getEnabled(), seed2.getEnabled());
		assertEquals(seed1.getStatistics().success, seed2.getStatistics().success);
		assertEquals(seed1.getStatistics().fail, seed2.getStatistics().fail);
		assertEquals(seed1.getStatistics().wait, seed2.getStatistics().wait);
	}
	
	@Test
	public void testCreateSeed() {
		Seed seed = mockSeed();
		String id = seedRepo.createSeed(seed);
		assertTrue(id != null);
	}
	
	@Test
	public void testGetSeed() {
		Seed mock = mockSeed();
		String id = seedRepo.createSeed(mock);
		
		Seed seed = seedRepo.getSeed(id);
		assertTrue(seed != null);
		
		compareSeed(seed, mock);
	}
	
	@Test
	public void testGetSeedList() {
		
		Seed mock1 = mockSeed();
		seedRepo.createSeed(mock1);
		
		Seed mock2 = mockSeed();
		seedRepo.createSeed(mock2);
	
		Seed mock3 = mockSeed();
		seedRepo.createSeed(mock3);
		
		List<Seed> seeds = seedRepo.getSeeds();
		assertEquals(seeds.size(), 3);
	}
	
	@Test
	public void testDeleteSeed() {
		Seed mock = mockSeed();
		String id = seedRepo.createSeed(mock);
		
		seedRepo.deleteSeed(id);
		
		Seed seed = seedRepo.getSeed(id);
		assertTrue(seed == null);
	}
	
	@Test
	public void testUpdateSeed() {
		Seed mock = mockSeed();
		String id = seedRepo.createSeed(mock);
		
		mock.setName("Updated");
		mock.setEnabled(false);
		mock.setReliability("COM");
		mock.setUrl("smb://test/");
		mock.setLevel("B");
		
		seedRepo.updateSeed(id, mock);
		
		Seed seed = seedRepo.getSeed(id);
		compareSeed(seed, mock);
	}
	
	@Test
	public void testGetEnabledSeed() {
		Seed mock = mockSeed();
		String id = seedRepo.createSeed(mock);
		List<Seed> seeds = seedRepo.getEnabledSeeds();
		assertEquals(seeds.size(), 1);
	}
	
	@Test
	public void testSeedSerialize() {
		Seed mock = mockSeed();
		seedRepo.createSeed(mock);
		seedRepo.createSeed(mock);
		seedRepo.createSeed(mock);
		
		List<Seed> seeds = seedRepo.getSeeds();
		Gson gson = new Gson();
		String json = gson.toJson(seeds);
		List<Seed> revertSeeds = gson.fromJson(
				json, 
				new TypeToken<List<Seed>>(){}.getType());
		for(Seed seed : revertSeeds) {
			compareSeed(seed, mock);
		}
		
	}
	
	
}
