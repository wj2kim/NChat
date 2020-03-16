package com.chat.netty.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.netty.channel.group.ChannelGroup;

public class RoomInfo {
	
	private String RoomName;
	private String RoomPassword;
	private String RoomMaster;
	private int maxNoOfUsers;
	private ChannelGroup channeGroup;
	private Map <String, UserInfo> usersInRoom;
	
	private List <String> blockedUsers = new ArrayList<String>();
	
	// 서버 통계 용 
	private int cumulativeCount = 0;
	private Date roomCreationDate;
	
	public RoomInfo() {
		
	}
	
	//사용자가 방 입장 시 && 방장이 방 생성  시
	public RoomInfo(String roomName, String roomMaster) {
		this.cumulativeCount ++;
		this.RoomName = roomName;
		this.RoomMaster = roomMaster;
	}
	
	
	// NChat Manager 가 방정보 생성 시 
	public RoomInfo(String roomName, String roomPassword, String roomMaster, int maxNoOfUsers ,ChannelGroup channelGroup, Date roomCreationDate) {
		this.RoomName = roomName;
		this.RoomPassword = roomPassword;
		this.RoomMaster = roomMaster;
		this.maxNoOfUsers = maxNoOfUsers;
		this.channeGroup = channelGroup;
		this.roomCreationDate = roomCreationDate;
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

	
	public int getMaxNoOfUsers() {
		return maxNoOfUsers;
	}

	public Map<String, UserInfo> getUsersInRoom() {
		return usersInRoom;
	}
		
	public ChannelGroup getChanneGroup() {
		return channeGroup;
	}

	public void setChanneGroup(ChannelGroup channeGroup) {
		this.channeGroup = channeGroup;
	}
	
	
	public int getCumulativeCount() {
		return cumulativeCount;
	}


	public Date getRoomCreationDate() {
		return roomCreationDate;
	}


	public boolean isblockedUser(String blockedUser) {
		if(blockedUsers != null && !blockedUsers.isEmpty()) {
			for(String users : blockedUsers) {
				if(users.equals(blockedUser)) {
					return true;
				}
			}			
		}
		return false;
	}
	
	public void setBlockedUser(String blockedUser) {
		if(blockedUsers != null ) {
			blockedUsers.add(blockedUser);			
		}
	}
	
	public static final String a = "없음";
	
	public String blockedUsersInString() {
		String users = "";
		if(blockedUsers.isEmpty()) {
			return a;
		}
		for(String user : blockedUsers) {
			users += user + " ";
		}
		return users.trim().replace(" ", ", ");
	}

	public String passwordPresence(){
		if (RoomPassword.isEmpty()) {
			return "없음";
		}
		return "있음";
	}

	public String usersInRoomInString() {
		String users = "";

		Iterator<String> mapIter = usersInRoom.keySet().iterator();
		while(mapIter.hasNext()) {
			users += mapIter.next()+" ";			
		}
		return users.trim().replaceAll(" ", ", ");
	}

	public void setUsersInRoom(Map<String, UserInfo> usersInRoom) {
		this.usersInRoom = usersInRoom;
	}
	
	public String roomSurvivedTimeInMin() {
		Date today = new Date();
		if(roomCreationDate != null) {
			long elapsedTime = today.getTime() - roomCreationDate.getTime();
			int elapsedTimeInSec = (int) (elapsedTime / 1000);
			int elapsedTimeInMin = elapsedTimeInSec / 60 ;
			int elapsedTimeInHour = elapsedTimeInMin / 60 ; 
			if(elapsedTimeInSec > 59 ) {
				elapsedTimeInSec -= elapsedTimeInMin * 60 ;
			}
			if(elapsedTimeInMin > 59 ) {
				elapsedTimeInMin -= elapsedTimeInHour *60 ; 
			}
			return elapsedTimeInHour + "시간 " + elapsedTimeInMin + "분 " + elapsedTimeInSec + "초 " ;			
		}
		return "";
	}
	
	

}
