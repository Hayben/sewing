package com.sidooo.point;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sidooo.item.Item;

@Repository("pointRepository")
public class PointRepository {
	
    @Autowired
    private MongoTemplate mongo;
	
	public long getPointCount() {
		return mongo.count(new Query(), Point.class);
	}
	
	public long getLinkCount() {
		return mongo.count(new Query(), Link.class);
	}
	

	public void createLink(Link link) {
    	mongo.insert(link);
	}
	
	public String createPoint(Point point) {
		mongo.insert(point);
		return point.getDocumentId();
	}
	
	public Point getPoint(String pointId) {
		return mongo.findById(pointId, Point.class);
	}
	
	public Link getLink(String keyword) {
		return mongo.findById(keyword, Link.class);
	}
	
	public void updateLink(Link link) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(link.getKeyword());
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("points", link.getPointList());
    	mongo.updateFirst(query, update, Link.class);
	}
	
	public void updatePoint(Point point) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(point.getDocumentId());
		query.addCriteria(criteria);
		
		Update update = new Update();
		update.set("title", point.getTitle());
		update.set("links", point.getLinks());
		
		mongo.updateFirst(query, update, Point.class);
	}
	
	public void clear() {
		mongo.dropCollection(Point.class);
		mongo.dropCollection(Link.class);
	}
	
    public Network search(String keyword, int depth) throws Exception {
    	
    	Network network = new Network();
    	
    	Link rootLink = getLink(keyword);
    	if (rootLink == null) {
    		return null;
    	}
    	network.addLink(rootLink);
    	
    	for(int i=0; i<depth; i++) {
    		
    		Link[] links = network.getLinks();
    		for(Link link : links) {
     			List<String> pointIdList = link.getPointList();
    			for(String pointId : pointIdList) {
    				Point point = getPoint(pointId);
    				if (point != null) {
    					network.addPoint(point);
    				}
    			}
    		}
    		
    		Point[] points = network.getPoints();
    		for(Point point : points) {
    			List<String> linkIdList = point.getLinks();
    			for(String linkId : linkIdList) {
    				Link link = getLink(linkId);
    				if (link != null) {
    					network.addLink(link);
    				}
    			}
    		}
    	}
    	
    	return network;
    }
	

}
