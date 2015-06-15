package com.sidooo.item;

import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;


@Repository("itemRepository")
public class ItemRepository {
	
	@Autowired
	private MongoTemplate mongo;
	
	public void clear() {
		mongo.dropCollection(Item.class);
	}
	
	public String md5(String content) {
		
    	MessageDigest digest;
    	try {
    		digest = MessageDigest.getInstance("MD5");
    	} catch(Exception e) {
    		return "ffffffff00000000";
    	}
    	
    	digest.update(content.getBytes());
    	byte[] md5 = digest.digest();
    	StringBuffer hexString = new StringBuffer();
    	for (int i = 0; i < md5.length; i++) {
          String shaHex = Integer.toHexString(md5[i] & 0xFF);
          if (shaHex.length() < 2) {
              hexString.append(0);
          }
          hexString.append(shaHex);
    	}
    	return hexString.toString();
	}
	
	public String saveItem(Item item) {
		item.id = md5(item.content);
		mongo.save(item);
		return item.id;
	}
	
	public Item getItem(String id) {
		return mongo.findById(id, Item.class);
	}

    
//	public void updateItem(Item item) {
//    	Query query = new Query();
//    	Criteria criteria = Criteria.where("_id").is(new ObjectId(item.getId()));
//    	query.addCriteria(criteria);
//    	
//    	Update update = new Update();
//    	update.set("format", item.getFormat());
//    	update.set("brief", item.getBrief());
//    	update.set("ontology", item.getOntology());
//    	update.set("content", item.getContent());
//    	update.set("logs", item.getLogs());
//    	update.set("comment", item.getComment());
//    	
//    	mongo.setWriteConcern(WriteConcern.SAFE);
//    	WriteResult wr = mongo.updateFirst(
//    			query, update, getCollectionName(item.getSeed()));
//    	System.out.println(wr.toString());
//	}
	
    public Item getItem(String seedId, String itemId) {
    	
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(itemId);
    	query.addCriteria(criteria);
    	
    	return (Item) mongo.findOne(query, Item.class);
    }
    
    public long getItemCount() {
    	return mongo.count(new Query(), Item.class);
    }
    
    public long getItemCountBySeed(String seedId) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("seed").is(seedId);
    	query.addCriteria(criteria);
    	return mongo.count(query, Item.class);
    }
    
    public List<Item> getItemList(String seedId, int skipCount, int limitCount) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("seed").is(seedId);
    	query.addCriteria(criteria);
    	query.skip(skipCount);
    	query.limit(limitCount);
    	
    	return mongo.find(query, Item.class);
    }
    
	public void updateItemContent(String seedId, String itemId, String content) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(itemId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("content", content);
    	
    	mongo.updateFirst(query, update, Item.class);
	}
	
	public void updateItemOntology(String seedId, String itemId, String ontology) {
    	Query query = new Query();
    	Criteria criteria = Criteria.where("id").is(itemId);
    	query.addCriteria(criteria);
    	
    	Update update = new Update();
    	update.set("ontology", ontology);
    	
    	mongo.updateFirst(query, update, Item.class);		
	}
}
