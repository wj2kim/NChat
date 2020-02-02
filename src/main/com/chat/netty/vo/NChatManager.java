package com.chat.netty.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NChatManager {
	
	static Map<String, UserInfo> allUsers = new HashMap<String, UserInfo>();
	static Map<String, RoomInfo> allRooms = new HashMap<String, RoomInfo>();
	
//	public NChatManager(Map<String, UserInfo>allUsers, Map<String, RoomInfo> allRooms) {
//		this.allUsers = allUsers;
//		this.allRooms = allRooms;
//	}
	public NChatManager() {
		// TODO Auto-generated constructor stub
	}
	
	public void setUser(UserInfo userInfo) {
		allUsers.put(userInfo.getUserName(), userInfo);
	}
	
	public String getUserName(UserInfo userInfo) {
		UserInfo user = allUsers.get(userInfo.getUserName());
		return user.getUserName();
	}
	
	public void removeUser(UserInfo userInfo) {
		allUsers.remove(userInfo.getUserName());
	}
	
	public void setRoom(RoomInfo roomInfo) {
		allRooms.put(roomInfo.getRoomMaster(), roomInfo);
	}
	
	public void removeRoom(RoomInfo roomInfo) {
		allRooms.remove(roomInfo.getRoomMaster());
	}
	
	public Map<String, UserInfo> getAllUsers() {
		return allUsers;
	}
	
	public void printAllUsers() {
		Iterator<String> keys = allUsers.keySet().iterator();
		while ( keys.hasNext() ) {
		    String key = keys.next();
		    System.out.println("방법3) key : " + key +" / value : " + allUsers.get(key));
		}   

	}
	
	public Map<String, RoomInfo> getAllRooms() {
		return allRooms;
	}

	


	
	
	
}
