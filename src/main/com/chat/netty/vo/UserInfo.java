package com.chat.netty.vo;

import com.chat.netty.reference.UserId;
import com.chat.netty.reference.UserName;

public class UserInfo {
	
	private UserId userId;
	private UserName userName;
	
	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

	public UserInfo(UserId userId, UserName userName) {
		this.userId = userId;
		this.userName = userName;
	}

	public UserId getUserId() {
		return userId;
	}

	public UserName getUserName() {
		return userName;
	}
	
	
	
	
	
	
	
	

}
