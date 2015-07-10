package com.sidooo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

	@Autowired
	private UserRepository userRepo;
	
	public User registerUser(String email, String password) throws Exception{
		
		if (userRepo.getUserByEmail(email) != null) {
			throw new Exception("User alredy exist.");
		}
		
		User user = new User();
		user.setRegisterTime(System.currentTimeMillis());
		user.setEmail(email);
		user.setPassword(password);
		user.setEnabled(true);
		user.setLevel(0);
		
		userRepo.createUser(user);
		return user;
	}
	
	public void disableUser(String userId) {
		userRepo.disableUser(userId);
	}
	
	public User getUser(String userId) {
		return userRepo.getUserById(userId);
	}
	
	public void updateUser(User user) {
		userRepo.updateUser(user);
	}
	
	public User login(String email, String password) throws Exception {
		User user = userRepo.getUserByEmail(email);
		if (user == null) {
			return null;
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		if (!password.equals(user.getPassword())) {
			return null;
		}
		
		return user;
	}
	
	public void upgrade(String userId, int newLevel) throws Exception {
		User user = userRepo.getUserById(userId);
		
		if (user == null) {
			throw new Exception("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		if (newLevel <= user.getLevel()) {
			throw new Exception("Invalid Level.");
		}
		
		user.setLevel(newLevel);
		userRepo.updateUser(user);
	}
	
	public void downgrade(String userId, int newLevel) throws Exception {
		User user = userRepo.getUserById(userId);
		if (user == null) {
			throw new Exception("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		if (newLevel >= user.getLevel()) {
			throw new Exception("Invalid Level.");
		}
		
		user.setLevel(newLevel);
		userRepo.updateUser(user);
	}
	
	public void subscribeKeyword(String userId, String keyword) throws Exception {
		User user = userRepo.getUserById(userId);
		
		if (user == null) {
			throw new Exception("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		if (user.existKeyword(keyword)) {
			throw new Exception("Keyword already exist.");
		}
		
		int maxCount = 0;
		switch(user.getLevel()) {
		case 0:
			maxCount = 1;
			break;
		case 1:
			maxCount = 5;
			break;
		case 2:
			maxCount = 10;
			break;
		case 3:
			maxCount = 30;
			break;
		case 4:
			maxCount = 100;
			break;
		case 5:
			maxCount = 9999;
			break;
		default:
			throw new Exception("Invalid User Level.");
		}
		
		if (user.getKeywordCount() >= maxCount) {
			throw new Exception("Keyword Count Max.");
		}
		
		user.addKeyword(keyword);
	}

	public void changePassword(String userId, String newPassword) throws Exception {
		
		User user = userRepo.getUserById(userId);
		
		if (user == null) {
			throw new Exception("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		user.setPassword(newPassword);
		
		userRepo.updateUser(user);
	}
	
	public void changeEmail(String userId, String email) throws Exception {
		
		User user = userRepo.getUserById(userId);
		
		if (user == null) {
			throw new Exception ("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		user.setEmail(email);
		
		userRepo.updateUser(user);
	}

	public void updatePayinfo(String userId, String bankno) throws Exception {
		User user = userRepo.getUserById(userId);
		
		if (user == null) {
			throw new Exception ("User not found.");
		}
		
		if (!user.isEnabled()) {
			throw new Exception("User is disabled.");
		}
		
		user.setBankNumber(bankno);
		userRepo.updateUser(user);
	}
	
	
}
