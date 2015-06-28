package com.sidooo.generator;


public class Generator extends Thread {
	
//	private String status = "IDLE";
//	
//	private List<String> logs = new ArrayList<String>();
//	
//	private Map<String, Boolean> types = new HashMap<String,Boolean>();
//	
//	private PointRepository repo;
//
//	public Generator(PointRepository repo) {
//		this.repo = repo;
//	}
//	
//	public void addLog(String message) {
//		this.logs.add(message);
//	}
//	
//	public List<String> getLogs() {
//		return logs;
//	}
//	
//	public String getStatus() {
//		return status;
//	}
//	
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	
//    private String loadJavaScript(String fileName) throws Exception {
//        ClassLoader classLoader = getClass().getClassLoader();
//        File file = new File(classLoader.getResource(fileName).getFile());
//        Path path = Paths.get(file.getAbsolutePath());
//        byte[] content = Files.readAllBytes(path);
//        String js = new String(content, "UTF-8");
//        return js;
//    }
//    
//    @Override
//    public void run() {
//    
//    	try {
//	    	generateLinks();
//	    	generatePoints();
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}
//    }
//	
//    public void generatePoints() throws Exception {
//    	
//		String	map3 = loadJavaScript("map3.js");
//		String	reduce3 = loadJavaScript("reduce3.js");
//		String	finalize3 = loadJavaScript("finalize3.js");
//		
//		if(isInterrupted()) {
//			return;
//		}
//		
//		
//		if (!repo.isExistCollection("temp")) {
//			return;
//		}
//		
//		status = "Generating Points";
//		DBCollection collection = repo.getCollection("temp");
//		
//		MapReduceCommand command = new MapReduceCommand(
//				collection,
//				map3,
//				reduce3,
//				"point",
//				OutputType.REPLACE,
//				null);
//		command.setVerbose(true);
//		//command.put("keeptemp", false);
//        //command.setFinalize(finalize3);
//        
//        MapReduceOutput output = collection.mapReduce(command);
//        status = "Generate Points finished";
//    }
//    
//    public void generateLinks() {
//		
//		DBCollection collTemp = repo.getCollection("temp");
//		collTemp.drop();
//		
//		DBCollection collLink = repo.getCollection("link");
//		collLink.drop();
//		
//		List<Attribute> dtList = repo.get();
//		for(DataType dt : dtList) {
//			types.put(dt.getId(), dt.getUnique());
//		}
//		
//		//get all seeds
//    	List<Seed> seeds = repo.getSeedList();
//		for (Seed seed : seeds) {
//			
//			if(isInterrupted()) {
//				break;
//			}
//			
//			String collName = "item_"+seed.getId();
//			if (!repo.isExistCollection(collName)) {
//				continue;
//			}
//			
//			status = "Generate Links ...";
//			DBCollection collection = repo.getCollection(collName);
//			
//			DBCursor cursor = collection.find();
//			while(cursor.hasNext()) {
//				
//				DBObject doc = cursor.next();
//
//				if (!isInterrupted() &&
//					doc.containsKey("ontology")) {
//					
//					DBObject newOnto = new BasicDBObject();
//					
//					Link link = new Link();
//					link.setId(seed.getId() + ":" + doc.get("_id").toString());
//					//link.setTitle(doc.get("brief").toString());
//					link.setTitle(seed.getConfig().getName());
//					
//					String str = doc.get("ontology").toString();
//					DBObject onto = (DBObject)JSON.parse(str);
//					Set<String> keys = onto.keySet();
//					for(String key : keys) {
//						String typeId = onto.get(key).toString();
//						if (types.get(typeId)) {
//							newOnto.put(key, typeId);
//							link.addKey(key);
//						}
//					}
//					DBObject item = new BasicDBObject();
//					item.put("_id", doc.get("_id").toString());
//					item.put("ontology", JSON.serialize(newOnto));
//					item.put("seed", seed.getId());
//					collTemp.insert(item,WriteConcern.SAFE);
//					
//					repo.createLink(link);
//				}
//			}
//			
//			status = "Generate Links finished.";
//		}
//    }
}
