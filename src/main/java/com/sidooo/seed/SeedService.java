package com.sidooo.seed;

import java.io.IOException;
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

	public Seed createSeed(Seed newSeed) throws IllegalArgumentException {
		List<Seed> seeds = seedRepo.getSeeds();
		for (Seed seed : seeds) {
			if (seed.getUrl().equalsIgnoreCase(newSeed.getUrl())) {
				throw new IllegalArgumentException("Seed already exist");
			}
		}
		seedRepo.createSeed(newSeed);
		return newSeed;
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

	public void updateFetchStatistics(String seedId, long successCount,
			long failCount, long waitCount, long limitCount) {

		Seed seed = seedRepo.getSeed(seedId);
		seed.setSuccessCount(successCount);
		seed.setFailCount(failCount);
		seed.setWaitCount(waitCount);
		seed.setLimitCount(limitCount);
		seedRepo.updateSeed(seedId, seed);
	}

	public void updateAnalysisStatistics(String seedId, long pointCount,
			long linkCount) {
		Seed seed = seedRepo.getSeed(seedId);
		seed.setPointCount(pointCount);
		seed.setLinkCount(linkCount);
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

	public Seed getSeedByUrl(String url) {
		List<Seed> seeds = seedRepo.getEnabledSeeds();
		for (Seed seed : seeds) {
			if (url.contains(seed.getUrl())) {
				return seed;
			}
		}
		return null;
	}

	public static Seed getSeedByUrl(String url, List<Seed> seeds) {
		for (Seed seed : seeds) {
			if (url.toLowerCase().startsWith(seed.getUrl().toLowerCase())) {
				return seed;
			}
		}
		return null;
	}

}
