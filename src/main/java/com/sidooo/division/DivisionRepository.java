package com.sidooo.division;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sidooo.seed.Seed;

@Repository("divisioinRepository")
public class DivisionRepository {
	
    @Autowired
    private MongoTemplate mongo;

    
    public Division getDivsionByName(String name) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("name").is(name);
    	query.addCriteria(criteria);
    	
    	return mongo.findOne(query, Division.class);
    }
    
    public List<Division> getAll() {
    	return mongo.findAll(Division.class);
    }
    
	public List<Division> getProvinceList() {
    	
    	Query query = new Query();
    	Criteria criteria = Criteria.where("level").is(1);
    	query.addCriteria(criteria);
    	
    	return mongo.find(query, Division.class);
    }
//    
//    @SuppressWarnings("unchecked")
//	public List<Division> getChildren(Integer parentId) {
//    	return sessionFactory.getCurrentSession()
//    			.createQuery("from Division where parentid=?")
//    			.setParameter(0, parentId)
//    			.list();
//    }
//    
//    @SuppressWarnings("unchecked")
//	public List<Division> getListByLevel(Integer level) {
//    	return sessionFactory.getCurrentSession()
//    			.createQuery("from Division where level=?")
//    			.setParameter(0, level)
//    			.list();
//	}

	public Division getDivision(Integer parentId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(parentId.intValue());
		return null;
	}
    

}
