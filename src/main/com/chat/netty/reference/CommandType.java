package com.chat.netty.reference;

public enum CommandType {
	// client -> Server 
	SET_USERNAME("setUserName"),
	PROGRAM_EXIT("exit"),
	CREATE_ROOM("createRoom"),
	ENTER_ROOM("enterRoom"),
	EXIT_ROOM("exitRoom"),
	ROOM_INFO("roomInfo"),
	CHANGE_ROOMNAME("changeRoomName"),
	REMOVE_ROOM_PASSWORD("removeRoomPassword"),
	INVITE_USER("inviteUser"),
	KICKOUT_USER("kickoutUser"),
	GET_ROOMLIST("getRoomList"),
	GET_USERLIST("getUserList"),
	GET_WAITINGROOM_USERLIST("getWaitingRoomUserList"),
	SEND_MESSAGE("sendMessage"),
	ACCOUNT_INFO("accountInfo"),
	
	// Server -> Client (false)
	NAME_AlREADY_EXIST("nameAlreadyExist"),
	
	// Server -> CLient (true)
	NAME_SET("nameSet")
	
	;
	
	private final String type;
	
	CommandType(String type){
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	
	public static CommandType forType(String type) {
		for(CommandType ct : CommandType.values()) {
			if(ct.type == type) {
				return ct;
			}
		}
		throw new IllegalArgumentException("유효하지 않습니다. "+type);
	}
}
