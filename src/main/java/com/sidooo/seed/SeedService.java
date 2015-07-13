package com.sidooo.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

	public void deleteSeed(String id) throws Exception {
		Seed seed = seedRepo.getSeed(id);
		if (seed == null) {
			throw new Exception("Seed not found");
		}
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
	
	public void incAnalysisStatistics(String seedId, long pointCount, long linkCount) {
		seedRepo.incAnalysisCount(seedId, pointCount, linkCount);
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
			if (seed.getUrl() == null) {
				continue;
			}
			if (url.toLowerCase().startsWith(seed.getUrl().toLowerCase())) {
				return seed;
			}
		}
		return null;
	}
	
	public Pagination getSeeds(int pageNo, int pageSize) {
		long totalCount = seedRepo.getSeedCount();
		Pagination page = new Pagination(pageNo, pageSize, totalCount);

		List<Seed> seeds = seedRepo.getSeeds(page.getFirstResult(), pageSize);		
		for (Seed seed : seeds) {
			page.addSeed(seed);
		}

		return page;
	}

	public void clearAnalysisStatistics(String id) {
		seedRepo.clearAnalysisStatistics(id);
	}
}
