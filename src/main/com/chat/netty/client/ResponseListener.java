package com.chat.netty.client;

import static com.chat.netty.view.ClientView.RoomLoop;
import static com.chat.netty.view.ClientView.doNotDisturb;
import static com.chat.netty.view.ClientView.createRoomRequest;
import static com.chat.netty.view.ClientView.globalMasterName;
import static com.chat.netty.view.ClientView.inRoom;
import static com.chat.netty.view.ClientView.invitationReceived;
import static com.chat.netty.view.ClientView.mainLoop;
import static com.chat.netty.view.ClientView.printAllUsers;
import static com.chat.netty.view.ClientView.roomInfo;
import static com.chat.netty.view.ClientView.selectRoomFromList;
import static com.chat.netty.view.ClientView.userInfo;
import static com.chat.netty.view.ClientView.welcomeScreen;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.chat.netty.reference.MessageType;
import com.chat.netty.reference.PacketFactory;
import com.chat.netty.vo.RoomInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.ChannelHandlerContext;

public class ResponseListener {
	
	protected static void printFactory(ChannelHandlerContext ctx, byte [] packet) {
		
		PacketFactory factory = new PacketFactory(packet);
		
		@SuppressWarnings("unused")
		int packetSize = factory.getSize(); // 내부적으로 getShort 메소드를 이용해 2 byte 크기의 패킷 전체 길이 정보를 가져온다.
		@SuppressWarnings("unused")
		int packetId = factory.getId(); // 4 bit 크기의 패킷 id
		
		byte messageType = factory.getMessageType(); // 1byte 크기의 MessageType 정보를 가져온다.
		 
		Date today = new Date();
		SimpleDateFormat time = new SimpleDateFormat("a hh:mm");
		
		MessageType type = MessageType.forType(messageType); 
		
		/* 처음에는 아무 생각 없이 if문을 사용했으나, switch 문으로 변경 함 
		 * if 문은 모든 조건에 대해서 compare (비교) 과정을 거치기 때문에 조건을 확인하기 위한 인스트럭션이 계속해서 필요하다. 
		 * switch 문은 일정 조건 수가 넘어가면 Jump Table을 만들어 그 안에서 값을 확인하고 바로 해당 코드로 넘어가는 방식으로 작동한다.
		 * 때문에 입력 받은 값을 확인 하는 인스트럭션만 있으면 된다. Junp Table을 생성하는데 오버헤드가 있다는 제한이 있지만 
		 * 해당 로직에서는 오버헤드를 크게 발생시킬만한 양을 사용하지 않으므로 switch 문을 선택했다.
		 */
		
		switch(type) {
		
		case CHAT_MESSAGE : {
			System.out.println( factory.getString() + " [ " + time.format(today) + " ] : "+ factory.getString());
		} break;
		case GREETING_MESSAGE : {
			System.out.println( factory.getString() + " 님이 입장했습니다.");
		} break;
		case ROOM_HAS_DESTROYED : {
			inRoom = false;
			System.out.println("해당 방이 비활성화 되었습니다. 대기실로 이동하시려면 엔터키를 누르십시오.");
			mainLoop(ctx);
		} break;
		case USER_EXIT_FROM_ROOM : {
			System.out.println( factory.getString() +" 님이 방에서 퇴장했습니다." );
		} break;
		case ANNOUNCEMENT : {
			System.out.println();
			System.out.println("< 공지 사항 > " + factory.getString() + " [ "+time.format(today) + " ] : " + factory.getString());
			System.out.println();
		} break;
		case KICKEDOUTFROMROOM : {
			inRoom = false;
			System.out.println("방에서 강제 퇴장 되셨습니다. 대기실로 이동하시려면 엔터키를 누르십시오.");
			mainLoop(ctx);
		} break;
		case PRIVATE_MESSAGE : {
			System.out.println("< 귓속말 > " +  factory.getString() + " [ " + time.format(today) + " ] : "+  factory.getString() );
		} break;
		case NAME_AlREADY_EXIST : {
			System.out.println("해당 닉네임은 이미 사용중입니다.");
			welcomeScreen(ctx);
		} break;
		case NO_ACTIVE_ROOMS : {
			System.out.println("활성화 된 방이 없습니다.");
			mainLoop(ctx);
		} break;
		case NO_ACTIVE_USERS : {
			System.out.println("활동 중인 사용자가 없습니다.");
		} break;
		case USER_NOT_FOUND : {
			System.out.println("해당 유저를 찾을 수 없습니다.");
		} break;
		case USER_AlREADY_IN_ROOM : {
			System.out.println("해당 유저는 이미 방에 있습니다.");
		} break;
		case CREATING_ROOM_RESTRICTED : {
			System.out.println("현재 방 생성에 제한이 걸려 방을 생성 할 수 없습니다. 대기실로 돌아갑니다.");
			mainLoop(ctx);
		} break;
		case USER_NOT_FOUND_IN_ROOM : {
			System.out.println("해당 유저는 방에 없습니다.");
		} break;
		case ENTRY_DENIED : {
			System.out.println("해당 방은 입장 하실 수 없습니다.");
			mainLoop(ctx);
		} break;
		case NAME_HAS_SET : {
			System.out.println("해당 닉네임으로 계정이 생성됬습니다. 대기실로 이동합니다.");
			userInfo = new UserInfo(factory.getString());
//			userInfo.setUserName(factory.getString());
			mainLoop(ctx);
		} break;
		case ABLE_TO_CREATE_ROOM : {
			System.out.println("방 생성 화면으로 이동합니다.");
			System.out.println();
			doNotDisturb = true;
			if(!createRoomRequest(ctx)) {doNotDisturb = false; mainLoop(ctx);};
		} break;
		case ROOM_HAS_SET : {
			roomInfo = new RoomInfo(factory.getString(), factory.getString()); // 방장 룸 객체 생성
			System.out.println("방 생성이 완료되었습니다. 해당 방으로 이동합니다.");
			doNotDisturb = false;
			inRoom = true;
			RoomLoop(ctx);
		} break;
		case EXIT_ROOM_COMPLETE : {
			System.out.println("해당 방과 연결을 끊었습니다. 대기실로 이동합니다.");
			inRoom = false;
			mainLoop(ctx);
		} break;
		case ENTER_ROOM_COMPLETE : {
			System.out.println("해당 방과 연결을 완료했습니다. 방으로 입장합니다.");
			roomInfo = new RoomInfo(factory.getString(), factory.getString());
			inRoom = true;
			RoomLoop(ctx);
		} break;
		case SEND_ROOM_INFO : {
			// 방 정보 보여주기 (방에 있는 유저 포함) 
			System.out.println(factory.getString());
		} break;
		case INVITATION_SENT : {
			// 초대장 분기 처리 
			if(!doNotDisturb) {
				invitationReceived = true;
				globalMasterName = factory.getString();
				System.out.println(globalMasterName + " 님이 당신을 초대했습니다. 해당 방으로 이동하시려면 y 를 입력해 주세요.");				
			}
		} break;
		case INVITATION_SENT_COMPLETE : {
			System.out.println("해당 유저에게 초대장을 보냈습니다.");
		} break;
		case UPDATE_COMPLETE : {
			System.out.println("수정이 완료 되었습니다.");
		} break;
		case PRIVATE_MSG_SENT : {
			System.out.println("해당 유저에게 귓속말을 보냈습니다.");
		} break;
		case SEND_ROOMLIST : {
			@SuppressWarnings("unchecked")
			List<String[]> allRooms = factoryToList(factory, "roomList");
			if(allRooms != null) {
				selectRoomFromList(ctx, allRooms);				
			}
		} break;
		case SEND_USERLIST : {
			@SuppressWarnings("unchecked")
			List<String> allUsers = factoryToList(factory, "userList");
			if(allUsers != null) {
				printAllUsers(ctx, allUsers);				
			}
			mainLoop(ctx);
		} break;
		default : break;	
		}
	}
		
	
	// list 가 담긴 패킷을 처리할 메소드.
	@SuppressWarnings("rawtypes") // SuppressWarning - 원시 유형 사용법과 관련된 경고를 억제
	public static List factoryToList (PacketFactory factory, String type) {
//		패킷에서 추출한 데이터를 담을 객체 생성. 숫자 옵션으로 선택이 가능하게끔 만들기 위한 순차적인 index를 할용하기 위해 List 객체를 이용.

		if(type.equals("roomList") && factory != null) { // 방 목록 
			List<String[]> allRooms = new ArrayList<String[]>();
			int length = factory.getSize();
			for(int i = 0 ; i < length; i++) {
				String [] roomInfo = { factory.getString(), factory.getString(), factory.getString(), factory.getString(), factory.getString() };
				allRooms.add(roomInfo);			
			}			
			return allRooms;
		}
		if(type.equals("userList") && factory != null) { // 유저 목록
			List<String> allUsers = new ArrayList<String>();
			int length = factory.getSize();
			for(int i = 0 ; i < length; i++) {
				allUsers.add(factory.getString());
			}
			return allUsers;
		}
		return null;
	}

}
