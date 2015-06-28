package com.sidooo.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("linkRepository")
public class LinkRepository {

    @Autowired
    private MongoTemplate mongo;
	
    public long getLinkCount() {
		return mongo.count(new Query(), Link.class);
	}
	
	public void createLink(Link link) {
    	mongo.insert(link);
	}
	
	public Link getLink(String keyword) {
		return mongo.findById(keyword, Link.class);
	}
	
	public void updateLink(Link link) {
		
    	Query query = new Query();
    	Criteria criteria = Criteria.where("_id").is(link.getKeyword());
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("type", link.getType());
    	update.set("points", link.getPointList());
    	mongo.updateFirst(query, update, Link.class);
	}
	
	public void clear() {
		mongo.dropCollection(Link.class);
	}
}
