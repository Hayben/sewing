package com.sidooo.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("seedRepo")
public class SeedRepository {

	@Autowired
	private MongoTemplate mongo;
	
	public String getDatabaseName() {
		return mongo.getDb().getName();
	}

    public List<Seed> getSeedsByDivision(Integer divisionId) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("config.division").is(divisionId);
    	query.addCriteria(criteria);
    	
    	return mongo.find(query, Seed.class);
    }
    
    public List<Seed> getSeeds() {
    	return mongo.findAll(Seed.class);
    }

    public Seed getSeed(String seedId) {

    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	return mongo.findOne(query, Seed.class);
    }
    
    public List<Seed> getEnabledSeeds() {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("enabled").is(true);
    	query.addCriteria(criteria);
    	
    	return mongo.find(query, Seed.class);
    }
    
    public String  createSeed(Seed seed){    	
    	mongo.insert(seed);
    	return seed.getId();
    }
    
    public void updateSeed(String seedId, Seed seed){
    	
    	mongo.save(seed);
    	
//    	Query query = new Query();
//    	Criteria criteria = Criteria.where("id").is(seedId);
//    	query.addCriteria(criteria);
//    	
//    	Update update = new Update();
//    	update.set("config", seedConfig);
//    	
//    	
//    	mongo.updateFirst(query, update, Seed.class);
    }
    
    public void updateSeedConfig(String seedId, Seed seed) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("name", seed.getName());
    	update.set("url", seed.getUrl());
    	update.set("enabled", seed.getEnabled());
    	update.set("type", seed.getType());
    	update.set("level", seed.getLevel());
    	update.set("reliability", seed.getReliability());
    	
    	mongo.updateFirst(query, update, Seed.class);
    }
    
    public void updateCrawlCount(String seedId, long successCount,
			long failCount, long waitCount, long limitCount) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("statistics.success", successCount);
    	update.set("statistics.fail", failCount);
    	update.set("statistics.wait", waitCount);
    	update.set("statistics.limit", limitCount);
    	
    	mongo.updateFirst(query, update, Seed.class);
    }
    
    public void incAnalysisCount(String seedId, long pointCount, long linkCount) {
    	
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.inc("statistics.point", pointCount);
    	update.inc("statistics.link", linkCount);
    	
    	mongo.updateFirst(query, update, Seed.class);
    }

    public void deleteSeed(String seedId) {

    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	mongo.findAndRemove(query, Seed.class);
    }
    
    public void clear() {
    	//update.set("config", seedConfig);
    	mongo.dropCollection(Seed.class);
    }
    
    public List<Seed> getSeeds(int skipCount, int limitCount) {
    	Query query = new Query();
    	query.skip(skipCount);
    	query.limit(limitCount);
    	
    	return mongo.find(query, Seed.class);
    }
    
    public long getSeedCount() {
    	return mongo.count(new Query(), Seed.class);
    }
	
	
}
