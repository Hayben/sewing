package com.sidooo.point;

import com.sidooo.ai.Keyword;
import com.sidooo.ai.Recognition;
import com.sidooo.point.Link;
import com.sidooo.point.Network;
import com.sidooo.point.NetworkStatus;
import com.sidooo.point.Point;
import com.sidooo.point.PointRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	@Autowired
	private LinkRepository linkRepo;
	
	@Autowired
	private ItemRepository itemRepo;
	
	private Recognition recog = new Recognition();
	
	public void clearItems() {
		itemRepo.clear();
	}
	
	public void clearLinks() {
		linkRepo.clear();
	}
	
	public void clearPoints() {
		pointRepo.clear();
	}
	
	public NetworkStatus getStatus() {
		NetworkStatus status = new NetworkStatus();
		status.pointCount = pointRepo.getPointCount();
		status.linkCount = linkRepo.getLinkCount();
		status.itemCount = itemRepo.getItemCount();
		return status;
	}
	
	public String addItem(Item item) {
		
		String itemId = itemRepo.saveItem(item);
		
		Keyword[] keywords = recog.search(item.getContent());
		if (keywords.length <= 0) {
			return itemId;
		}
		
		Point point = pointRepo.getPoint(itemId);
		if (point == null) {			
			point = new Point();
			point.setDocId(itemId);
			point.setTitle(item.getTitle());
			for(Keyword keyword : keywords) {
				point.addLink(keyword);
			}
			pointRepo.createPoint(point);
		} else {
			point.setTitle(item.getTitle());
			point.removeLinks();
			for(Keyword keyword : keywords) {
				point.addLink(keyword);
			}
			pointRepo.updatePoint(point);
		}
		
		for(Keyword keyword : keywords) {
			
			Link link = linkRepo.getLink(keyword.getWord());
			if (link == null) {
				link = new Link();
				link.setKeyword(keyword.getWord());
				link.setType(keyword.getAttr());
				link.addPoint(itemId);
				linkRepo.createLink(link);
			} else {
				if (!link.existPoint(itemId)) {
					link.addPoint(itemId);
					linkRepo.updateLink(link);
				}
			}
			

		}
		
		return itemId;
	}
	
    public Network search(String keyword, int depth) throws Exception {
    	
    	Network network = new Network();
    	
    	Link rootLink = linkRepo.getLink(keyword);
    	if (rootLink == null) {
    		return null;
    	}
    	network.addLink(rootLink);
    	
    	for(int i=0; i<depth; i++) {
    		
    		Link[] links = network.getLinks();
    		for(Link link : links) {
     			String[] pointIdList = link.getPointList();
    			for(String pointId : pointIdList) {
    				Point point = pointRepo.getPoint(pointId);
    				if (point != null) {
    					network.addPoint(point);
    				}
    			}
    		}
    		
    		Point[] points = network.getPoints();
    		for(Point point : points) {
    			Keyword[] linkIdList = point.getLinks();
    			for(Keyword linkId : linkIdList) {
    				Link link = linkRepo.getLink(linkId.getWord());
    				if (link != null) {
    					network.addLink(link);
    				}
    			}
    		}
    	}
    	
    	return network;
    }
    
	public Pagination getItemList(String seedId, int pageNo, int pageSize) {

		long totalCount = itemRepo.getItemCountBySeed(seedId);
		Pagination page = new Pagination(pageNo, pageSize, totalCount);

		List<Item> items = itemRepo.getItemList(seedId, page.getFirstResult(),
				pageSize);
		for (Item item : items) {
			page.addItem(item);
		}

		return page;
	}

	public long getItemCountBySeed(String seedId) {
		return itemRepo.getItemCountBySeed(seedId);
	}

	public Item getItem(String id) {
		return itemRepo.getItem(id);
	}

}
