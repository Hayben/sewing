package com.sidooo.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("countService")
public class CountService {
	
	@Autowired
	private RedisClientTemplate template;
	
	public void incPointCount(String seedId) {
		template.incr(seedId+".point");
	}
	
	public void incPointCount(String seedId, long count) {
		template.incrBy(seedId+".point", count);
	}
	
	public void incLinkCount(String seedId) {
		template.incr(seedId+".link");
	}
	
	public void incLinkCount(String seedId, long count) {
		template.incrBy(seedId+".link", count);
	}
	
	public long getPointCount(String seedId) {
		if (!template.exists(seedId + ".point")) {
			return 0;
		}
		return Long.parseLong(template.get(seedId + ".point"));
	}
	
	public long getLinkCount(String seedId) {
		if (!template.exists(seedId + ".link")) {
			return 0;
		}
		return Long.parseLong(template.get(seedId + ".link"));
	}
	
	public void resetCount(String seedId) {
		template.del(seedId + ".point");
		template.del(seedId + ".link");
	}

}
