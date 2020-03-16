package com.chat.netty.reference;

public enum MessageType {
//	<<<< Client -> Server>>>> 
//	(Create)
	SET_USERNAME((byte)0), // String userName
	SET_ROOMPASSWORD((byte)1), // String roomPassword
	CREATE_ROOM((byte)2), // String roomName, String password
	CREATE_ROOM_CONFIRMATION((byte)3), // - 

//	(Read)
	GET_ROOMLIST((byte)4), // - 
	GET_USERLIST((byte)5), // -
	GET_ROOMINFO((byte)6), // - 
	GET_USERINFO((byte)7), // - 
	GET_USERLISTINROOM((byte)8), // -

//	(Update)
	CHANGE_ROOMNAME((byte)9), // String roomName
	CHANGE_ROOMPASSWORD((byte)10), //String roomPassword
	ENTER_ROOM((byte)11), // String roomName, String roomPassword
	INVITE_USER((byte)12), // String userName
	
//	(Delete)
	KICKOUT_USER((byte)13), //String userName
	REMOVE_ROOMPASSWORD((byte)14), // - 
	EXIT_ROOM((byte)15), // - 
	PROGRAM_EXIT((byte)16), // - 
	
//	(Extra)
	PRIVATE_MESSAGE((byte)17), //-
	PRIVATE_MSG_SENT((byte)18), // =
	
//	<<<< Server -> Client>>>> 
//	(chat)
	CHAT_MESSAGE((byte)19),
	GREETING_MESSAGE((byte)20),
	
	
//	(ObjectResponse)
	SEND_ROOMLIST((byte)21), // Map<String,RoomInfo>allRooms
	SEND_USERLIST((byte)22), // Map<String, UserInfo>allUsers
	
//	(InfoResponse)
	SEND_USER_INFO((byte)23), // UserInfo userInfo
	SEND_ROOM_INFO((byte)24), // RoomInfo roomInfo
	
//	(negative) 
	NAME_AlREADY_EXIST((byte)25), // - 
	NO_ACTIVE_ROOMS((byte)26), // -
	ROOM_HAS_DESTROYED((byte)27), // -
	NO_ACTIVE_USERS((byte)28), // - 
	USER_NOT_FOUND((byte)29), // -
	USER_AlREADY_IN_ROOM((byte)30), // -
	ABLE_TO_CREATE_ROOM((byte)31),
	USER_NOT_FOUND_IN_ROOM((byte)32), //-
	KICKEDOUTFROMROOM((byte)33), // -
	ENTRY_DENIED((byte)34), // -
	
//	(positive)
	NAME_HAS_SET((byte)35), // -
	ROOM_HAS_SET((byte)36), // -
	EXIT_ROOM_COMPLETE((byte)37), // -
	ENTER_ROOM_COMPLETE((byte)38), // -
	USER_EXIT_FROM_ROOM((byte)39), // -
	INVITATION_SENT((byte)40), //-
	INVITATION_SENT_COMPLETE((byte)41), // -
	CREATING_ROOM_RESTRICTED((byte)42), // -
	KICKOUT_USER_COMPLETE((byte)43), // -
	UPDATE_COMPLETE((byte)44), //-
	
//	(server)
	ANNOUNCEMENT((byte)45) // -
	;
	
	
	private final byte inByte;
	
	MessageType(byte inByte){
		this.inByte = inByte;
	}

	
	public byte getByte() {
		return inByte;
	}
	
	public static MessageType forType(byte inByte) {
		for(MessageType mType : MessageType.values()) {
			if(mType.inByte == inByte) {
				return mType;
			}
		}
		throw new IllegalArgumentException("유효하지 않습니다. " + inByte);
	}
}
