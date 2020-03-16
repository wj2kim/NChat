package com.chat.netty.server;

import static com.chat.netty.reference.ByteArrayFactory.byteArrayFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.chat.netty.reference.MessageType;
import com.chat.netty.reference.NChatManager;
import com.chat.netty.reference.PacketFactory;
import com.chat.netty.reference.TemporaryPacketStorage;
import com.chat.netty.vo.RoomInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RequestListener {
	
	private static final Logger LOGGER = Logger.getLogger(RequestListener.class.getName());
	
	private static final int maxNumberOfRooms = 10;
	private static final int maxNumberOfUsersInRoom = 6;
	
	protected static final NChatManager manager = new NChatManager();
	
	protected final static AttributeKey <UserInfo> user = AttributeKey.valueOf("userInfo");
	private final static AttributeKey <RoomInfo> room = AttributeKey.valueOf("roomInfo");
	
	private static ChannelGroup roomChannelGroup ;
	private static int packetId;
	
	
	protected static void responseFactory(ChannelHandlerContext ctx, byte [] packet, long startTime) {
		
		String userName = ctx.channel().attr(user).get().getUserName();
		PacketFactory factory = new PacketFactory(packet);		
		
		@SuppressWarnings("unused")
		int packetSize = factory.getSize(); // useless data
		
		packetId = factory.getId(); 
		byte messageType = factory.getMessageType(); // 1byte 크기의 MessageType 정보를 가져온다.
		
		byte [] resPacket; // 응답용 패킷 byte 배열
		
		
		MessageType type = MessageType.forType(messageType); 
				
		LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + ( userName == null ? "미설정" : userName ) +" ] REQUEST [ " + type + " ] ");
		
		/* 처음에는 아무 생각 없이 if문을 사용했으나, switch 문으로 변경 함 
		 * if 문은 모든 조건에 대해서 compare (비교) 과정을 거치기 때문에 조건을 확인하기 위한 인스트럭션이 계속해서 필요하다. 
		 * switch 문은 일정 조건 수가 넘어가면 Jump Table을 만들어 그 안에서 값을 확인하고 바로 해당 코드로 넘어가는 방식으로 작동한다.
		 * 때문에 입력 받은 값을 확인 하는 인스트럭션만 있으면 된다. Junp Table을 생성하는데 오버헤드가 있다는 제한이 있지만 
		 * 해당 로직에서는 오버헤드를 크게 발생시킬만한 양을 사용하지 않으므로 switch 문을 선택했다.
		 */
		switch (type) {
		
		case SET_USERNAME : {
			resPacket = setUserName(ctx, factory.getString());
			if(resPacket != null) {				
				sendToClient(ctx, type, "미설정" , resPacket, startTime);
			} 
		} break;
		case CREATE_ROOM : {
			resPacket = createRoom(ctx, userName, factory.getString(), factory.getString());
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);
			} 
		} break;
		case CREATE_ROOM_CONFIRMATION : {
			resPacket = createRoomConfirmation(ctx, userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case GET_ROOMLIST : {
			resPacket = getRoomList(ctx, userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			} 
		} break;
		case EXIT_ROOM : { 
			resPacket = exitRoom(ctx, userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			} 
		} break;
		case ENTER_ROOM : {
			resPacket = enterRoom(ctx, userName, factory.getString());
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			} 
		} break;
		case CHAT_MESSAGE : { // chat message는 수 많은 패킷의 처리를 담당하기도 하고 속도를 중시 해야 함으로 임시 저장소에 저장 했다가 지우는 절차를 생략함. 
			chatMessageHandler(ctx, userName, factory.getString(), startTime);
		} break;
		case GET_USERLIST : {
			resPacket = getUserList(ctx, userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case GET_ROOMINFO : {
			resPacket = getRoomInfo(ctx, factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case INVITE_USER : {
			resPacket = inviteUser(ctx, factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case KICKOUT_USER : {
			resPacket = kickoutUser(ctx, factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case CHANGE_ROOMNAME : {
			resPacket = changeRoomInfo(ctx, type, factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case CHANGE_ROOMPASSWORD : {
			resPacket = changeRoomInfo(ctx, type, factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		case PRIVATE_MESSAGE : {
			resPacket = privateMessage(ctx, factory.getString(),  factory.getString(), userName);
			if(resPacket != null) {
				sendToClient(ctx, type, userName, resPacket, startTime);				
			}
		} break;
		default : break;	
		}
	}
	

	public static void makeAnnouncement() { // 서버 용
		// 레퍼런스에 따르면 nanoTime 메소드는 현재 JAVA 가상 머신의 시간 값을 nano second 단위로 반환하고 이 메소드는 오직 
		// 경과된 시간을 측정하는데 사용해야 한다 하기에 사용함. 시스템 시각(시/분/초) 와는 아무런 연관성이 없다고 한다. 
		System.out.println("공지사항을 입력해 주세요.");
		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			line = br.readLine();
		} catch (IOException e) {
			LOGGER.error("입력 도중 에러가 발생했습니다.", e);
		}
		long startTime = System.nanoTime(); 
		for(Channel channel : manager.getGlobalChannel()) { // 서버와 연결된 모든 소켓 채널에게 
			channel.writeAndFlush(byteArrayFactory(MessageType.ANNOUNCEMENT, "서버 운영자", line));
		}
		long endTime = System.nanoTime();
		double elapsedTimeInSec = ((double)(endTime - startTime ))/1_000_000_000; //두 시간의 실행 차 계산 후 초로 변환.
		LOGGER.info("이름 [ 서버 운영자 ] 메소드 [ makeAnnouncement() ] 실행 시간 [ " + elapsedTimeInSec + " 초 ] ");
	}
	
	
	 private synchronized static byte [] setUserName(final ChannelHandlerContext ctx , final String userName) { // thread-safe
		UserInfo userInfo = ctx.channel().attr(user).get();
		if(isUserNameTaken(userName)) { // 중복 체킹 후 분기 처리
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ 미설정 ] RESPONSE [ " + MessageType.NAME_AlREADY_EXIST +" ] 중복 닉네임 [ " + userName + " ]");
			return byteArrayFactory(MessageType.NAME_AlREADY_EXIST);
		}else {
			// 서버에 유저 정보 저장하고 클라이언트에게 처리 완료 응답 보내기
			userInfo.setUserName(userName);
			manager.setUser(userInfo); // 매니저 유저관리 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ 미설정 ] RESPONSE [ " + MessageType.NAME_HAS_SET +" ] 설정 한 닉네임 [ " + userName + " ] ");
			return byteArrayFactory(MessageType.NAME_HAS_SET, userName);
		}
	}
	
	 
	private static boolean isUserNameTaken(final String userName) {
		//	NChatManager을 이용해 닉네임 중복 체크
		if(manager.getAllUsers() == null || manager.getAllUsers().isEmpty()
				|| ! manager.getAllUsers().containsKey(userName)) {
			return false;
		}
		return true;
	}
	
	
	private static byte [] createRoomConfirmation(final ChannelHandlerContext ctx, final String userName) {		 		
		// 방 생성 여부 확인 작업 
		if(isRoomsToMax()) { // 생성 제한 체킹 후 분기 처리
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ userName +" ] RESPONSE [ " + MessageType.CREATING_ROOM_RESTRICTED + " ] 설정한 최대 방 갯수 [ " + maxNumberOfRooms +"개에 도달함 ]");
			return byteArrayFactory(MessageType.CREATING_ROOM_RESTRICTED);
		}else {
			int roomsLeft = maxNumberOfRooms - manager.getAllRooms().size();
			// 생성 가능
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ userName +" ] RESPONSE [ " + MessageType.ABLE_TO_CREATE_ROOM + " ] 최대 방 갯수까지 [ " + roomsLeft + "개 남음 ] ");
			return byteArrayFactory(MessageType.ABLE_TO_CREATE_ROOM);
		}
	}
	
	private static boolean isRoomsToMax() {
		if(!(manager.getAllRooms().size() < maxNumberOfRooms)) { // NChatManger 이용 
			return true;
		}
		return false;
	}
	

	private synchronized static byte [] createRoom(final ChannelHandlerContext ctx, final String userName, final String roomName, final String roomPassword) { // thread-safe
		if(isRoomsToMax()) {
			// 다시 한번 생성 제한 체크
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ userName +" ] RESPONSE [ " + MessageType.CREATING_ROOM_RESTRICTED + " ] 설정한 최대 방 갯수 [ " + maxNumberOfRooms +"개에 도달함 ]");
			return byteArrayFactory(MessageType.CREATING_ROOM_RESTRICTED);
		}
		Date today = new Date(); // 방 생성일 		
		roomChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE); // 방에 접속한 유저들의 소켓 채널 관리
//		ctx.channel().attr(group).set(roomChannelGroup);
		roomChannelGroup.add(ctx.channel());
		
		UserInfo userInfo = ctx.channel().attr(user).get();
		RoomInfo roomInfo = new RoomInfo(roomName, roomPassword ,userName , maxNumberOfUsersInRoom ,roomChannelGroup, today);
		if(userInfo != null && roomInfo != null) {
			ctx.channel().attr(room).set(roomInfo);
			Map<String, UserInfo>usersInRoom = new HashMap<String, UserInfo>(); // 방 안에 있는 유저 정보 관리
			usersInRoom.put(userName , userInfo);
			roomInfo.setUsersInRoom(usersInRoom);
			manager.setRoom(roomInfo); // 매니저가 방 정보 관리 
			
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.ROOM_HAS_SET + " ] 생성 된 방 제목 [ " + roomInfo.getRoomName() + " ]");
			return byteArrayFactory(MessageType.ROOM_HAS_SET, roomInfo.getRoomName(), roomInfo.getRoomMaster());
		}
		return null;
	}
	
	
	private static byte[] getRoomList(final ChannelHandlerContext ctx, final String userName) {		 
//		// 매니저 객체로 부터 모든 방 정보 가지고 오기 
		Map<String, RoomInfo> allRooms = manager.getAllRooms();
		if(!allRooms.isEmpty()) {
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.SEND_ROOMLIST + " ]");
			return byteArrayFactory(MessageType.SEND_ROOMLIST, allRooms, "roomInfo");

		}else {
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.NO_ACTIVE_ROOMS + " ]");
			return byteArrayFactory(MessageType.NO_ACTIVE_ROOMS);
		}
	}
	
	
	private static byte [] exitRoom(final ChannelHandlerContext ctx, final String userName) {	 

		UserInfo userInfo = ctx.channel().attr(user).get();
		RoomInfo roomInfo = ctx.channel().attr(room).get();
		ChannelGroup chGroup = roomInfo.getChanneGroup();
		String exitRoom = roomInfo.getRoomName(); // 로그 용도
		
		ctx.channel().attr(room).set(null); // 채널.attribute 초기화
		
		if(userName == roomInfo.getRoomMaster()) {
			// 임시 채널 그룹 저장소 만들기
			ChannelGroup temp = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
			temp.addAll(chGroup);
			//방장일 경우 
			boolean removeAllComplete = chGroup.removeAll(chGroup);
			chGroup.clear();
//			채널 그룹에서 모든 사용자 연결 끊기
			if(removeAllComplete) {
//				모든 사용자 연결 끊기가 성공적으로 완료 방안의 모든 사용자 강제로 방에서 나가게기.
//				ringBuffer sb = new StrinbBuilder();
				LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ctx.channel().attr(user).get().getUserName()+" ] RESPONSE [ " + MessageType.ROOM_HAS_DESTROYED + " ] 종료한 방 제목 [ " + exitRoom + " ] 방장이 방을 종료합니다.");
				for(Channel channel : temp) {
					if(!ctx.channel().equals(channel)) {
						channel.attr(room).set(null);
						LOGGER.info("IP:포트 주소 [ " + channel.remoteAddress() +" ] 닉네임 [ "+channel.attr(user).get().getUserName()+" ] RESPONSE [ " + MessageType.ROOM_HAS_DESTROYED + " ] 종료한 방 제목 [ " + exitRoom + " ] 방장이 방을 종료하여 대기실로 강제 이동합니다.");			
						channel.writeAndFlush(byteArrayFactory(MessageType.ROOM_HAS_DESTROYED));
					}
				}
			}
			temp.removeAll(temp);
			temp.clear();
			manager.removeRoom(roomInfo);
		}else {
			//일반 사용자들
			for(Channel channel : chGroup) {
				if(!ctx.channel().equals(channel)) {
					channel.writeAndFlush(byteArrayFactory(MessageType.USER_EXIT_FROM_ROOM, userName));
				}
			}
			chGroup.remove(ctx.channel());
			manager.removeUserFromRoom(roomInfo, userInfo);
		}
		LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ctx.channel().attr(user).get().getUserName()+" ] RESPONSE [ " + MessageType.EXIT_ROOM_COMPLETE +" ] 나간 방 제목 [ " + exitRoom + " ] ");
		return byteArrayFactory(MessageType.EXIT_ROOM_COMPLETE);
	}
	
	
	
	private static byte [] enterRoom(final ChannelHandlerContext ctx, final String userName, final String masterName) { // thread-safe
		UserInfo userInfo = ctx.channel().attr(user).get();
		RoomInfo roomInfo = manager.getAllRooms().get(masterName);
		// 방장 이름으로 되어 있는 key 값으로 roomInfo 를 불러와 
		
		if(roomInfo!=null) {
			// 퇴장 경력 여부 확인후 경력이 있다면 입장 거부.
			if(roomInfo.isblockedUser(userInfo.getUserName())) {
				LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.ENTRY_DENIED +" ] 방 입장 거부됨 [ " + roomInfo.getRoomName() + " ] 사유 [ 해당 방 강퇴 기록 있음 ] ");
				return byteArrayFactory(MessageType.ENTRY_DENIED);
			}
			// 방 수요 인원 확인 후 다 찾으면 입장 거부.
			if(roomInfo.getUsersInRoom().size() >= maxNumberOfUsersInRoom) {
				LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.ENTRY_DENIED +" ] 방 입장 거부됨 [ " + roomInfo.getRoomName() + " ] 사유 [ 해당 방 인원 수 제한에 도달 ] ");
				return byteArrayFactory(MessageType.ENTRY_DENIED);
			}
			ctx.channel().attr(room).set(roomInfo);
			roomInfo.getChanneGroup().add(ctx.channel());
			roomInfo.getUsersInRoom().put(userInfo.getUserName(), userInfo); // roomInfo 안에있는 usersInRoom map 에 해당 유저 정보 추가 
		
			ChannelGroup chGroup = roomInfo.getChanneGroup();
			for(Channel channel : chGroup) {
				if(!ctx.channel().equals(channel)) {
					channel.writeAndFlush(byteArrayFactory(MessageType.GREETING_MESSAGE, userName));
				}
			}
		}
		LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.ENTER_ROOM_COMPLETE +" ] 입장 한 방 제목 [ " + roomInfo.getRoomName() + " ] ");
		return byteArrayFactory(MessageType.ENTER_ROOM_COMPLETE, roomInfo.getRoomName(), roomInfo.getRoomMaster());
	}
	
	
	private static void chatMessageHandler(final ChannelHandlerContext ctx, final String userName, final String msg, long startTime) {	 
		
		// 일반적인 채팅 메시지 관리
		RoomInfo roomInfo = ctx.channel().attr(room).get();
		if(roomInfo == null ) { // 방이 갑작스럽게 폭파됬을 경우
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] 방장이 방을 폭파하여 대기실로 강제 이동 ");
			return; 
		}
		ChannelGroup chGroup = roomInfo.getChanneGroup();
		if(chGroup != null ) {
			for(Channel channel : chGroup) {
				if(!ctx.channel().equals(channel)) {
					LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.CHAT_MESSAGE +" ] 메시지 내용 [ "+ msg +" ] ");
					channel.writeAndFlush(byteArrayFactory(MessageType.CHAT_MESSAGE, userName, msg));
				}
			}				
		}
		long endTime = System.nanoTime();
		double elapsedTimeInSec = ((double)(endTime - startTime ))/1_000_000_000; //두 시간의 실행 차 계산 후 초로 변환.
		LOGGER.info("닉네임 [ " + userName + " ] 요청 종류 [ " + MessageType.CHAT_MESSAGE  + " ] 요청 처리 시간 [ " + elapsedTimeInSec + " 초 ] ");
	}
	
	
	private static byte []  getUserList(final ChannelHandlerContext ctx, final String userName) {	 
		
		// 매니저 객체에서 등록 된 모든 유저 정보 가지고 오기 
		Map<String, UserInfo> allUsers = manager.getAllUsers();
		if(!allUsers.isEmpty()) {
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ userName +" ] RESPONSE [ " + MessageType.SEND_USERLIST + " ]");
			return byteArrayFactory(MessageType.SEND_USERLIST, allUsers, "userInfo");
		}else {
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+ userName +" ] RESPONSE [ " + MessageType.NO_ACTIVE_USERS + " ]");
			return byteArrayFactory(MessageType.NO_ACTIVE_USERS);
		}
	}
	
	
	private static byte [] getRoomInfo(final ChannelHandlerContext ctx, final String masterName, final String userName) {
		RoomInfo roomInfo = manager.getAllRooms().get(masterName);
		String roomInfoToString = "";
		if(roomInfo != null ) {
			roomInfoToString = "방 제목 [ " + roomInfo.getRoomName() + 
					" ] 방 비밀번호 [ " + roomInfo.passwordPresence() + 
		    		" ] 방장 [ " + roomInfo.getRoomMaster() + " ] " + 
		    		" 참여 인원 [ " + roomInfo.getUsersInRoom().size() + "/" + roomInfo.getMaxNoOfUsers() + " ] " +
		    		" 참여자 [ " + roomInfo.usersInRoomInString() + " ] ";
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.SEND_ROOM_INFO + " ]");
			return byteArrayFactory(MessageType.SEND_ROOM_INFO, roomInfoToString);
			
		}else {
			LOGGER.error("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] 님의 방 정보 요청 처리 중 해당 방이 폭파됨 ");
		}
		return null;
	}
	
	
	private static byte [] inviteUser(final ChannelHandlerContext ctx, final String inviteName, final String userName) {
		RoomInfo roomInfo = ctx.channel().attr(room).get();
		Map<String,RoomInfo>allRooms = manager.getAllRooms();
		UserInfo userInfo = null;
		boolean isInRoom = false;
		
		// 해당 유저가 존재하는지 먼저 확인
		if(!manager.getAllUsers().isEmpty()) {
			Iterator<String> keys = manager.getAllUsers().keySet().iterator();		
			while (keys.hasNext()) {
				String key = keys.next();
				if(key.equals(inviteName)) {
					userInfo = manager.getAllUsers().get(key);
				}
			}
		}
		// 채팅방안에 유저가 존재하는지 확인
		if(!allRooms.isEmpty()) {
			Iterator<String> keys = allRooms.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();			
				Map<String, UserInfo>usersInRoom= allRooms.get(key).getUsersInRoom();
					Iterator<String> names = usersInRoom.keySet().iterator();
					while(names.hasNext()) {
					String name = names.next();
					if(name.equals(inviteName)) {
						isInRoom = true;
					}
				}
			}
		}
	
	
//		if(!allRooms.isEmpty()) {
//			Iterator<String> kes = allRooms.keySet().iterator();
//			int brCnt= 10;
//			while(keys.hasNext()) {
//				tring ke = keys.next();
//				++brCnt;
//				if ( Br >1 ) }
//			break;
//				
//				Map<String, UserInfo>usersInRoom= allRooms.get(key).getUsersInRoom();
//					Iterator<String> names = usersInRoom.keySet().iterator();
//					while(names.hasNext()) {
//					String name = names.next();
//					if(name.equals(inviteName)) {
//						isInRoom = true;
//					}
//				}
//				
//			}
//		}
	
	
		
		if(userInfo!=null && !isInRoom) {
			// 유저가 존재하고 방에 없음 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.INVITATION_SENT + " ] 초대 한 유저 닉네임 [ " + inviteName + " ] ");
			// 해당 유저에게 초대 보내기
			userInfo.getCtx().channel().writeAndFlush(byteArrayFactory(MessageType.INVITATION_SENT, roomInfo.getRoomMaster()));

			// 초대장을 성공적으로 보냈다는 메시지 보내기 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.INVITATION_SENT_COMPLETE + " ] 초대 한 유저 닉네임 [ " + inviteName + " ] ");
			return byteArrayFactory(MessageType.INVITATION_SENT_COMPLETE);
		}
		else if(userInfo!=null && isInRoom) {
			// 유저가 존재하고 방에 이미 있음 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.USER_AlREADY_IN_ROOM + " ] 찾는 닉네임 [ " + inviteName + " ] ");
			return byteArrayFactory(MessageType.USER_AlREADY_IN_ROOM);
		}else {
			// 유저가 존재하지 않음 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.USER_NOT_FOUND + " ] 찾는 닉네임 [ " + inviteName + " ] ");
			return byteArrayFactory(MessageType.USER_NOT_FOUND);
		}		
	}
	
	
	private static byte [] kickoutUser(final ChannelHandlerContext ctx, final String kickoutName, final String userName ) {
		RoomInfo roomInfo = ctx.channel().attr(room).get();
		UserInfo userInfo = null;
		boolean isInRoom = false;
		if(roomInfo != null) {
			Iterator<String> keys = roomInfo.getUsersInRoom().keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				if(key.equals(kickoutName)) {
					isInRoom = true;
					userInfo = roomInfo.getUsersInRoom().get(key);
					break;
				}	
			}
		}
		if(isInRoom) {
			// 입력한 유저가 방안에 있음
			ChannelGroup chGroup = roomInfo.getChanneGroup();
			for (Channel channel : chGroup ) {
				if(channel.attr(user).get().getUserName().equals(userInfo.getUserName())) {
					// 강퇴 요청을 한 유저의 channel을 channelGroup에서 제거
					chGroup.remove(channel);
//					channel.attr(group).set(null);
					channel.attr(room).set(null);
					// 강퇴 요청을 한 유저에게 응답 알림. 
					channel.writeAndFlush(byteArrayFactory(MessageType.KICKEDOUTFROMROOM));
				}else {
					channel.writeAndFlush(byteArrayFactory(MessageType.USER_EXIT_FROM_ROOM, userInfo.getUserName()));
				}
			}
			// 방의 강퇴목록에 추가
			roomInfo.setBlockedUser(userInfo.getUserName());
			// 매니저 객체의 roomInfo 에서 해당 유저 제거 
			manager.removeUserFromRoom(roomInfo, userInfo);
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.KICKOUT_USER_COMPLETE + " ] 강퇴한 유저 닉네임 [ " + kickoutName + " ] ");

		}else {
			// 입력한 유저가 방안에 없음 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.USER_NOT_FOUND_IN_ROOM + " ] 찾는 유저 닉네임 [ " + kickoutName + " ] ");
			return byteArrayFactory(MessageType.USER_NOT_FOUND_IN_ROOM);
		}
		return null;
	}
	
	
	private static byte [] changeRoomInfo(final ChannelHandlerContext ctx, final MessageType type, final String param, final String userName) {	 

		RoomInfo roomInfo = ctx.channel().attr(room).get();

		if(roomInfo != null) {
			// 요청이 방 제목 변경일 경우 
			if(type == MessageType.CHANGE_ROOMNAME) {
				roomInfo.setRoomName(param);
				LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.UPDATE_COMPLETE + " ] 변경 된 방 제목 [ " + roomInfo.getRoomName() + " ]");
				// 요청이 방 비밀번호 변경일 경우
			}else if(type == MessageType.CHANGE_ROOMPASSWORD) {
				roomInfo.setRoomPassword(param);
				LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ "+userName+" ] RESPONSE [ " + MessageType.UPDATE_COMPLETE + " ] 비밀번호 변경");
			}
		return byteArrayFactory(MessageType.UPDATE_COMPLETE);
		}
		return null;
	}
	
	
	private static byte [] privateMessage(final ChannelHandlerContext ctx, final String sendTo, final String content, final String userName) {	 

		UserInfo userInfo = ctx.channel().attr(user).get();

		// 입력한 닉네임으로 유저 찾기
		UserInfo ReceiverInfo = manager.getAllUsers().get(sendTo);
		if(userInfo != null && ReceiverInfo != null ) {
			// 찾는 유저가 존재 할 때
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.PRIVATE_MESSAGE + " ] 귓속말 대상 [ " + sendTo + " ] ");
			// 해당 유저에게 메시지 보내기 
			ReceiverInfo.getCtx().channel().writeAndFlush(byteArrayFactory(MessageType.PRIVATE_MESSAGE, userName, content));

			// 보낸이에게 성공적으로 보냈다고 응답해주기
			return byteArrayFactory(MessageType.PRIVATE_MSG_SENT);
		}else {
			// 찾는 유저가 없을 때 
			LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] RESPONSE [ " + MessageType.USER_NOT_FOUND + " ] 귓속말 대상 [ " + sendTo + " ] ");
			return byteArrayFactory(MessageType.USER_NOT_FOUND);
		}
	}

	
	// 요청 보낸 클라이언트에게 답장 하는 메소드
	private static void sendToClient(ChannelHandlerContext ctx, MessageType type, String userName, byte [] resPacket, long startTime) {
	   	ChannelFuture future =  ctx.writeAndFlush(resPacket);
	   	future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					// temporaryStorage 에 있는 패킷 아이디 지워줌 
					TemporaryPacketStorage.checkIdandRemove(packetId);
					long endTime = System.nanoTime();
					double elapsedTimeInSec = ((double)(endTime - startTime ))/1_000_000_000; //두 시간의 실행 차 계산 후 초로 변환.
					LOGGER.info("닉네임 [ " + userName + " ] 요청 종류 [ " + type  + " ] 요청 처리 시간 [ " + elapsedTimeInSec + " 초 ] 요청에 대한 응답처리 [ 완료 ]");
				}
			}
		});
	}
	

}
