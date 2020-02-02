package com.chat.netty.vo;

import java.util.Map;

public class NChatManager {

	private Map<String, AllUserInfo>allUsers;
	private Map<String, RoomInfo>allRooms;
	
	public NChatManager() {
		// TODO Auto-generated constructor stub
	}

	public Map<String, AllUserInfo> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(Map<String, AllUserInfo> allUsers) {
		this.allUsers = allUsers;
	}

	public Map<String, RoomInfo> getAllRooms() {
		return allRooms;
	}

	public void setAllRooms(Map<String, RoomInfo> allRooms) {
		this.allRooms = allRooms;
	}
	
	
	
}