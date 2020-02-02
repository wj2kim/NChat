package com.chat.netty.vo;

import java.util.Map;

public class UserManager {
	
	private Map<String, UserInfo> usersInChatRoom;
	private Map<String, UserInfo> usersInWaitingRoom;
	
	public UserManager() {
		// TODO Auto-generated constructor stub
	}

	public Map<String, UserInfo> getUsersInChatRoom() {
		return usersInChatRoom;
	}

	public void setUsersInChatRoom(Map<String, UserInfo> usersInChatRoom) {
		this.usersInChatRoom = usersInChatRoom;
	}

	public Map<String, UserInfo> getUsersInWaitingRoom() {
		return usersInWaitingRoom;
	}

	public void setUsersInWaitingRoom(Map<String, UserInfo> usersInWaitingRoom) {
		this.usersInWaitingRoom = usersInWaitingRoom;
	}
	
	

}
