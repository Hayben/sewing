package com.sidooo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public class UserRepository {

	@Autowired
	private MongoTemplate mongo;
	
	public User getUserByEmail(String email) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("email").is(email);
    	query.addCriteria(criteria);
    	
    	return mongo.findOne(query, User.class);
	}
	
	public User getUserById(String id) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(id);
    	query.addCriteria(criteria);
    	
    	return mongo.findOne(query, User.class);
	}
	
	public String createUser(User user) {
		mongo.insert(user);
		return user.getId();
	}
	
	public void disableUser(String userId) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(userId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("enabled", false);
    	mongo.updateFirst(query, update, User.class);
	}
	
	public void enableUser(String userId) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(userId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("enabled", true);
    	mongo.updateFirst(query, update, User.class);
	}
	
	public void updateUser(User user) {
		Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(user.getId());
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("username", user.getUsername());
    	update.set("password", user.getPassword());
    	update.set("email", user.getEmail());
    	
    	mongo.updateFirst(query, update, User.class);
	}
}
