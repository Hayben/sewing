package com.sidooo.serest;

import com.sidooo.point.Link;
import com.sidooo.point.Network;
import com.sidooo.point.NetworkStatus;
import com.sidooo.point.Point;
import com.sidooo.point.PointRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




import java.util.Iterator;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-2-11
 * Time: 下午9:39
 * To change this template use File | Settings | File Templates.
 */

@Service("pointService")
public class PointService {
	
	@Autowired
	private PointRepository pointRepo;
	
	public NetworkStatus getStatus() {
		NetworkStatus status = new NetworkStatus();
		status.pointCount = pointRepo.getPointCount();
		status.linkCount = pointRepo.getLinkCount();
		return status;
	}

	public Network search(String keyword, int depth) throws Exception {
		return pointRepo.search(keyword, depth);
	}

//
//    public JSONArray getPointList(String ontoname) throws Exception {
//
//        DBCollection collection = db.getCollection("points");
//        BasicDBObject query = new BasicDBObject();
//        query.put("ontology", ontoname);
//        DBCursor cursor= collection.find(query);
//
//        JSONArray points = new JSONArray();
//        while(cursor.hasNext()) {
//            DBObject item = cursor.next();
//            String json = JSON.serialize(item);
//            JSONObject point = new JSONObject(json);
//            String id = ((JSONObject)point.get("_id")).getString("$oid");
//            point.put("id", id);
//            point.remove("_id");
//            //JSONObject point = new JSONObject(item.toMap());
//            points.put(point);
//        }
//        return points;
//    }
//
//
//    public JSONArray getPointCount() throws Exception {
//
//        DBCollection collection = db.getCollection("statistics");
//        DBCursor cursor= collection.find();
//
//        JSONArray result = new JSONArray();
//        while(cursor.hasNext()) {
//            DBObject item = cursor.next();
//            JSONObject json = new JSONObject();
//            json.put("ontology", item.get("ontology").toString());
//            json.put("count", Integer.parseInt(item.get("count").toString()));
//            result.put(json);
//        }
//        return result;
//    }
//
//
//
//
//
//    public JSONObject getBlock(String title) throws Exception{
//        MongoOptions options = new MongoOptions();
//        options.autoConnectRetry = true;
//        options.connectionsPerHost = 200;
//        options.socketTimeout = 2000;
//        options.socketKeepAlive = true;
//
//        Mongo client = new Mongo(new ServerAddress(host, port), options);
//        DB db = client.getDB(dbname);
//        if (db == null) {
//            throw new ClassNotFoundException(dbname + " not found");
//        }
//        DBCollection collection = db.getCollection("blocks");
//        BasicDBObject query = new BasicDBObject();
//        query.put("_id", title);
//
//        DBObject item= collection.findOne(query);
//        if (item == null) {
//            return null;
//        }
//
//        JSONObject json = new JSONObject(JSON.serialize(item));
//        json.put("title", json.getString("_id"));
//        json.remove("_id");
//        return json;
//    }
//
//    public List<String> getLinks(String title) throws Exception {
//        MongoOptions options = new MongoOptions();
//        options.autoConnectRetry = true;
//        options.connectionsPerHost = 200;
//        options.socketTimeout = 2000;
//        options.socketKeepAlive = true;
//
//        Mongo client = new Mongo(new ServerAddress(host, port), options);
//        DB db = client.getDB(dbname);
//        if (db == null) {
//            throw new ClassNotFoundException(dbname + " not found");
//        }
//
//        DBCollection collection = db.getCollection("links");
//        BasicDBObject query = new BasicDBObject();
//        query.put("_id", title);
//
//        List<String> links = new ArrayList<String>();
//        DBObject item= collection.findOne(query);
//        if (item == null) {
//            return links;
//        }
//
//        JSONObject json = new JSONObject(JSON.serialize(item));
//        JSONArray list = json.getJSONArray("value");
//        for(int i=0; i<list.length(); i++) {
//            String link = list.getString(i);
//            links.add(link);
//        }
//        return links;
//    }
}
