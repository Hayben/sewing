package com.sidooo.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidooo.queue.QueueRepository;

@Service("seedService")
public class SeedService {
	
	@Autowired
	private SeedRepository seedRepo;
	
	public Seed getSeed(String id) {
		return seedRepo.getSeed(id);
	}
	
	public Seed createSeed(Seed seed) {
		seedRepo.createSeed(seed);
		return seed;
	}
	
	public void deleteSeed(String id) {
		Seed seed = seedRepo.getSeed(id);
		seedRepo.deleteSeed(id);
	}
	
	public void updateSeed(String id, Seed seed) {
		seedRepo.updateSeed(id, seed);
	}

	public List<Seed> getSeeds() {
		return seedRepo.getSeeds();
	}
	
	public List<Seed> getEnabledSeeds() {
		return seedRepo.getEnabledSeeds();
	}
	
	public void updateStatistics(String seedId, Statistics stat) {
		
		Seed seed = seedRepo.getSeed(seedId);
		seed.setStatistics(stat);
		seedRepo.updateSeed(seedId, seed);
	}

	public boolean toggleSeed(String id) {
		
		Seed seed = seedRepo.getSeed(id);
		if (seed == null) {
			return false;
		}
		
		boolean enabled = seed.getEnabled();
		seed.setEnabled(!enabled);
		
		seedRepo.updateSeed(id, seed);
		return seed.getEnabled();
	}


}
