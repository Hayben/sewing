package com.sidooo.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository("seedRepo")
public class SeedRepository {

	@Autowired
	private MongoTemplate mongo;

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

    public void deleteSeed(String seedId) {

    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(seedId);
    	query.addCriteria(criteria);
    	
    	mongo.findAndRemove(query, Seed.class);
    }
    
    public void clear() {
    	mongo.dropCollection(Seed.class);
    }
	
	
}
