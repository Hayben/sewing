package com.sidooo.ontology;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Repository
@Service("ontologyRepository")
public class OntologyRepository {
	
    @Autowired
    private MongoTemplate mongoTemplate;
    //private SessionFactory mongodbFactory;
    
    public List<Category> getCategroyList() {
    	return mongoTemplate.findAll(Category.class);
    }
    
    public Category getCategory(String id) {
    	return mongoTemplate.findById(id, Category.class);
    }
    
    public String createCategory(Category c) {
    	mongoTemplate.insert(c);
    	return c.getId().toString();
    }
    
    public void updateCategoryName(String id, String name) {
    	Query query = new Query(Criteria.where("id").is(id));
    	Update update = new Update();
    	mongoTemplate.updateFirst(query, update.set("name", name), Category.class);
    }
    
    public void deleteCategory(String id) {
    	Query query = new Query(Criteria.where("id").is(id));
    	mongoTemplate.findAndRemove(query, Category.class);
    }
    
    public boolean existCategory(String name) {
    	Query query = new Query(Criteria.where("name").is(name));
    	long count = mongoTemplate.count(query, Category.class);
    	return count > 0;
    }
    
    public List<Ontology> getOntologyList(String categoryId) {
    	Query query = new Query(Criteria.where("category").is(categoryId));
    	return mongoTemplate.find(query, Ontology.class);
    }
    
    public Ontology getOntology(String id) {
    	return mongoTemplate.findById(id, Ontology.class);
    }
    
    public String createOntology(Ontology o) {
    	mongoTemplate.insert(o);
    	return o.getId();
    }
    
    public void updateOntologyTitle(String ontoId, String title) {
    	Query query = new Query(Criteria.where("id").is(ontoId));
    	Update update = new Update();
    	mongoTemplate.updateFirst(query, update.set("title", title), Ontology.class);
    }
    
    public void updateOntologyDescription(String ontoId, String description) {
    	Query query = new Query(Criteria.where("id").is(ontoId));
    	Update update = new Update();
    	mongoTemplate.updateFirst(query, 
    			update.set("description", description), 
    			Ontology.class);
    }
    
    public void updateOntologyCategory(String ontoId, String categoryId) {
    	Query query = new Query(Criteria.where("id").is(ontoId));
    	Update update = new Update();
    	update.set("category", categoryId);
    	mongoTemplate.updateFirst(query, update, Ontology.class);
    }
    
    public void deleteOntology(String id) {
    	Query query = new Query(Criteria.where("id").is(id));
    	mongoTemplate.findAndRemove(query, Ontology.class);
    }
    
    public String createMember(String ontoId, Member member) {
    	member.setId(new ObjectId().toString());
    	Query query = new Query(Criteria.where("id").is(ontoId));
    	Update update = new Update();
    	mongoTemplate.updateFirst(query, 
    			update.push("members", member), 
    			Ontology.class);
    	
    	return member.getId();
    }
    
    public void deleteMember(String ontoId, Member member) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(ontoId);
    	query.addCriteria(criteria);
//    	criteria = Criteria.where("members").elemMatch(Criteria.where("id").is(member.getId()));
//    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.pull("members", member);
    	
    	mongoTemplate.updateFirst(query, update, Ontology.class);
    }
    
    public void updateMember(String ontoId, Member member) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(ontoId);
    	query.addCriteria(criteria);
    	//criteria = Criteria.where("members").elemMatch(Criteria.where("id").is(member.getId()));
    	criteria = Criteria.where("members._id").is(new ObjectId(member.getId()));
    	query.addCriteria(criteria);
    	
    	@SuppressWarnings("unused")
		long count = mongoTemplate.count(query, "ontology");
    	
    	Update update = new Update();
    	update.set("members.$.title", member.getTitle());
    	update.set("members.$.type", member.getType());
    	
    	mongoTemplate.updateFirst(query, update, Ontology.class);    	
    }

    
}
