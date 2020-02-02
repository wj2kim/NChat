package com.chat.netty.vo;

import java.util.Map;

public class RoomInfo {
	
	private int RoomId;
	private String RoomName;
	private String RoomPassword;
	private String RoomMaster;
	private Map <String, UserInfo> usersInRoom;
	
	public RoomInfo() {
		// TODO Auto-generated constructor stub
	}

	public int getRoomId() {
		return RoomId;
	}

	public void setRoomId(int roomId) {
		RoomId = roomId;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public String getRoomPassword() {
		return RoomPassword;
	}

	public void setRoomPassword(String roomPassword) {
		RoomPassword = roomPassword;
	}

	public String getRoomMaster() {
		return RoomMaster;
	}

	public void setRoomMaster(String roomMaster) {
		RoomMaster = roomMaster;
	}

	public Map<String, UserInfo> getUsersInRoom() {
		return usersInRoom;
	}

	public void setUsersInRoom(Map<String, UserInfo> usersInRoom) {
		this.usersInRoom = usersInRoom;
	}
	
	

}
