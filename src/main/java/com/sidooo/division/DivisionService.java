package com.sidooo.division;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("divisionService")
public class DivisionService {

	@Autowired
    private DivisionRepository divisionRepo;
	
//	private JSONArray cache = null;
//	
//	private JSONObject findDivision(JSONArray tree, Integer id) throws Exception{
//
//		for(int i=0; i < tree.length(); i++) {
//			JSONObject division = tree.getJSONObject(i);
//			if (division.getInt("id") == id) {
//				return division;
//			}
//			
//			if (division.has("nodes")) {
//				JSONArray children = division.getJSONArray("nodes");
//				if (children == null) {
//					continue;
//				}
//				
//				JSONObject result = findDivision(children, id);
//				if (result != null ) {
//					return result;
//				}
//			}
//
//		}
//		
//		return null;
//	}
//	
//	@Transactional
//	public JSONArray getDivisionOfCHN() {
//		if (cache == null || cache.length() <= 0) {
//			cache = new JSONArray();
//
//			  for(Integer i=0; i <= 4; i++) {
//				  List<Division> divisions = divisionRepo.getListByLevel(i);
//				  for(Division division : divisions) {
//					  
//					  try {
//						  JSONObject json = new JSONObject();
//						  json.put("text", division.getName());
//						  json.put("id", division.getId());
//					  
//					
//					  
//						  Integer parentId = division.getParentId();
//						  if (parentId == null || parentId == -1) {
//							  cache.put(json);
//						  } else {
//							  JSONObject parent = findDivision(cache, parentId);
//							  if (!parent.has("nodes")) {
//								  parent.put("nodes", new JSONArray());
//							  } 
//								  
//							  
//							  JSONArray nodes = parent.getJSONArray("nodes");
//							  nodes.put(json);
//						  }
//					  } catch (Exception e) {
//						  continue;
//					  }
//				  }
//			  }
//		}
//		
//		return cache;
//	}
//	
	
}
