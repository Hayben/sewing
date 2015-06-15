package com.sidooo.serest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidooo.queue.QueueRepository;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedRepository;

@Service("seedService")
public class SeedService {
	
	@Autowired
	private SeedRepository seedRepo;
	
	@Autowired
	private QueueRepository queue;
	
	public Seed getSeed(String id) {
		return seedRepo.getSeed(id);
	}
	
	public Seed createSeed(Seed seed) {
		seedRepo.createSeed(seed);
		queue.sendSeed(seed);
		return seed;
	}
	
	public void deleteSeed(String id) {
		Seed seed = seedRepo.getSeed(id);
		seedRepo.deleteSeed(id);
		queue.sendSeed(seed);
	}
	
	public void updateSeed(String id, Seed seed) {
		seedRepo.updateSeed(id, seed);
		queue.sendSeed(seed);
	}

	public List<Seed> getSeeds() {
		return seedRepo.getSeeds();
	}



}
