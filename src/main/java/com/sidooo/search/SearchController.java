package com.sidooo.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sidooo.point2.Graph;
import com.sidooo.point2.Point2Service;
import com.sidooo.user.User;
import com.sidooo.user.UserService;

@Controller
public class SearchController {

	@Autowired
	private Point2Service pointService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody Graph search(@RequestParam String key,
			@RequestParam int depth) throws Exception {
		Graph graph = pointService.getGraph(key);
		if (graph != null) {
			System.out.println("Point:" + graph.getNodeCount() + ",Keyword:"
					+ graph.getEdgeCount());
		}
		return graph;
	}

	@RequestMapping(value = "/user/register", method = RequestMethod.GET)
	public @ResponseBody User register(@RequestParam String email,
			@RequestParam String password) {
		try {
			return userService.registerUser(email, password);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public @ResponseBody User login(@RequestParam String email,
			@RequestParam String password) {
		try {
			return userService.login(email, password);
		} catch(Exception e) {
			return null;
		}
		
	}

	@RequestMapping(value = "/user/subscribe", method = RequestMethod.GET)
	public @ResponseBody boolean subscribe(@RequestParam String userId,
			@RequestParam String keyword) {

		try {
			userService.subscribeKeyword(userId, keyword);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping(value="/user/upgrade", method = RequestMethod.GET)
	public @ResponseBody boolean upgradeUser(@RequestParam String userId,
			int level) {
		try {
			userService.upgrade(userId, level);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping(value="/user/downgrade", method = RequestMethod.GET)
	public @ResponseBody boolean downgradeUser(@RequestParam String userId,
			int level) {
		try {
			userService.downgrade(userId, level);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@RequestMapping(value="/user/changeemail", method = RequestMethod.GET)
	public @ResponseBody boolean changeEmail(@RequestParam String userId,
			String newEmail) {
		try {
			userService.changeEmail(userId, newEmail);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping(value="/user/changepassword", method = RequestMethod.GET)
	public @ResponseBody boolean changePassword(@RequestParam String userId,
			String newPassword) {
		try {
			userService.changePassword(userId, newPassword);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@RequestMapping(value="/user/updatepay", method = RequestMethod.GET)
	public @ResponseBody boolean updatePayinfo(@RequestParam String userId, String bankno) {
		try {
			userService.updatePayinfo(userId, bankno);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
