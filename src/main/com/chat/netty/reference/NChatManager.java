package com.chat.netty.reference;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.chat.netty.vo.RoomInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NChatManager { // 서버에 연결된 모든 유저 정보, 방 정보, 소켓 채널 정보 를 관리 
	
	static Map<String, UserInfo> allUsers = new HashMap<String, UserInfo>(); // 생성된 모든 유저 정보

	static Map<String, RoomInfo> allRooms = new HashMap<String, RoomInfo>(); // 생성된 모든 방 정보
	
	static ChannelGroup globalChannel = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE); // 서버로 연결된 모든 소켓 관리 용도
	

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
	
	public void removeUser(String userName) {
		allUsers.remove(userName);
	}
	
	public void setRoom(RoomInfo roomInfo) {
		allRooms.put(roomInfo.getRoomMaster(), roomInfo);
	}

	public ChannelGroup getGlobalChannel() {
		return globalChannel;
	}


	public void removeRoom(RoomInfo roomInfo) {
		allRooms.remove(roomInfo.getRoomMaster());
	}
	
	public Map<String, UserInfo> getAllUsers() {
		return allUsers;
	}
	
	
	public Map<String, RoomInfo> getAllRooms() {
		return allRooms;
	}

	public static void setAllRooms(Map<String, RoomInfo> allRooms) {
		NChatManager.allRooms = allRooms;
	}

	public void removeUserFromRoom(RoomInfo roomInfo, UserInfo userInfo) {
		try {
			allRooms.get(roomInfo.getRoomMaster()).getUsersInRoom().remove(userInfo.getUserName());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printAllUsers() { // 서버 유저 목록
		if(allUsers.isEmpty()) {
			System.out.println("등록한 유저가 없습니다."); return;
			}
		SimpleDateFormat time = new SimpleDateFormat("yyyy년 MM월 dd일 E요일  a HH:mm:ss");
		Iterator<String> keys = allUsers.keySet().iterator();
		System.out.println("------------------------------- 유저 목록 -----------------------------");
		int index = 1;
		while ( keys.hasNext() ) {
			String key = keys.next();
			System.out.println(index + ") 닉네임 [ "+ key +" ] - IP:포트 주소 [ " 
			+ allUsers.get(key).getCtx().channel().remoteAddress() + " ] "
			+ "유저 생성일 [ " + time.format(allUsers.get(key).getUserCreationDate()) + " ] ");
			index ++;
		}   
		System.out.println("--------------------------------------------------------------------");			
	}
	
	
	public static void printAllRooms() { // 서버 방 목록 
		if(allRooms.isEmpty()) {
			System.out.println("활성화 된 방이 없습니다."); return;
		}
		SimpleDateFormat time = new SimpleDateFormat("yyyy년 MM월 dd일 E요일  a HH:mm:ss");
		System.out.println("------------------------------- 방 목록 ------------------------------");
		Iterator<String> keys = allRooms.keySet().iterator();
		int index = 1;
		while ( keys.hasNext() ) {
		    String key = keys.next();
		    RoomInfo roomInfo = allRooms.get(key);
		    System.out.println(index + ") 방 제목 [ " + roomInfo.getRoomName() + 
		    		" ] 방 비밀번호 [ " + roomInfo.passwordPresence() + 
		    		" ] 방장 [ " + key + " ] " + 
		    		" 참여 인원 [ " + roomInfo.getUsersInRoom().size() + "/"+ roomInfo.getMaxNoOfUsers() +" ] " +
		    		" 참여자 [ " + roomInfo.usersInRoomInString() + " ] " +
		    		" 강퇴자 [ " + roomInfo.blockedUsersInString() + " ] " +
		    		" 방 생성일 [ " + time.format(roomInfo.getRoomCreationDate()) + " ] " +
		    		" 방 생존시간 [ " + roomInfo.roomSurvivedTimeInMin() + " ] "
		    		);
		    index ++;
		}   
		System.out.println("--------------------------------------------------------------------");	
	}
	


	
	
	
}
