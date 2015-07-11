package com.sidooo.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sidooo.point.Item;

@Repository("rcSeedRepo")
public class RCSeedRepository {
	
	@Autowired
	private MongoTemplate mongo;
	
	public long getSeedCount() {
		return mongo.count(new Query(), RCSeed.class);
	}
	
	private void addSeed(RCSeed seed) {
		mongo.save(seed);
	}
	
    public List<RCSeed> getSeedList(int skipCount, int limitCount) {
    	Query query = new Query();
    	query.skip(skipCount);
    	query.limit(limitCount);
    	
    	return mongo.find(query, RCSeed.class);
    }
	

}
