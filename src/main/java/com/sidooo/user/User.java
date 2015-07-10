package com.sidooo.user;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "user")
public class User {

	@Id
	private String id;
	
	@Field("register")
	private long registerTime;
	
	@Field("email")
	private String email;
	
	@Field("username")
	private String username;
	
	@Field("password")
	private String password;
	
	@Field("enabled")
	private boolean enabled;
	
	@Field("expire")
	private long expireTime;
	
	@Field("bankno")
	private String bankno;
	
	@Field("level")
	private int level;
	
	@Field("keywords")
	private Map<String, String> keywords = new HashMap<String, String>();

	public String getId() {
		return this.id;
	}
	
	public void setRegisterTime(long regtime) {
		this.registerTime = regtime;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getKeywordCount() {
		return this.keywords.size();
	}

	
	public boolean existKeyword(String keyword) {
		return this.keywords.get(keyword) != null;
	}
	
	public void addKeyword(String keyword) {
		this.keywords.put(keyword, null);
	}

	public void setBankNumber(String bankno) {
		this.bankno = bankno;
	}
	
}
