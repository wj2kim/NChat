package com.chat.netty.vo;

public class UserInfo {
	
	private String userName;
	private String userId;
	
	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

	public UserInfo(String userName, String userId) {
		this.userName = userName;
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	
	
	
	
	
	
	
	

}
