package com.sidooo.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sidooo.point.Network;
import com.sidooo.point.PointService;


@Controller
public class SearchController {
	
	@Autowired
	private PointService pointService;
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody Network search(@RequestParam String key,
			@RequestParam int depth) throws Exception {
		Network network = pointService.search(key, depth);
		if (network != null) {
			System.out.println("Point:" + network.getPoints().length + ",Link:"
					+ network.getLinks().length);	
		}
		return network;
	}
}
