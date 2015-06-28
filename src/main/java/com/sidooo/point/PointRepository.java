package com.sidooo.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("pointRepository")
public class PointRepository {
	
    @Autowired
    private MongoTemplate mongo;


	public long getPointCount() {
		return mongo.count(new Query(), Point.class);
	}

	public String createPoint(Point point) {
		mongo.insert(point);
		return point.getDocId();
	}
	
	public Point getPoint(String pointId) {
		return mongo.findById(pointId, Point.class);
	}
	
	public void updatePoint(Point point) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(point.getDocId());
		query.addCriteria(criteria);
		
		Update update = new Update();
		update.set("title", point.getTitle());
		update.set("links", point.getLinks());
		
		mongo.updateFirst(query, update, Point.class);
	}
	
	public void clear() {
		mongo.dropCollection(Point.class);
	}
	

	

}
