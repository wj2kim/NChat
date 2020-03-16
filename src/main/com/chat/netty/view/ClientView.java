package com.chat.netty.view;

import static com.chat.netty.reference.ByteArrayFactory.byteArrayFactory;
import static com.chat.netty.reference.DisposablePacketStorage.setToStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chat.netty.reference.MessageType;
import com.chat.netty.vo.RoomInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientView {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	private final static ExecutorService pool = Executors.newSingleThreadExecutor();
	
	public static boolean inRoom = false;
	public static boolean doNotDisturb = false;
	public static boolean invitationReceived = false;
	
	public static UserInfo userInfo;
	public static RoomInfo roomInfo;
	public static String globalMasterName;
	
	private static String version = "version 1.0.0";
	
	// welcomeScreen - 첫 화면
	public static void welcomeScreen(ChannelHandlerContext ctx) {
		System.out.println();
		System.out.print("==============================================================================================\r\n");
		System.out.print("=============  ======= =========   ======= ======== ========== =========           ===========\r\n");
		System.out.print("============= = ====== ======= ===== ===== ======== ========= = ============= ================\r\n");
		System.out.print("============= == ===== ===== ============= ======== ======== === ============ ================\r\n");
		System.out.print("============= === ==== ==== ==============          =======       =========== ================\r\n");
		System.out.print("============= ==== === ===== ============= ======== ====== ======= ========== ================\r\n");
		System.out.print("============= ===== == ======= ===== ===== ======== ===== ========= ========= ================\r\n");
		System.out.print("============= ======   =========   ======= ======== ==== =========== ======== ================\r\n");
		System.out.print("==============================================================================================\r\n");
		System.out.print(version +"\r\n");
		String singleContent = setUserName(); 
		sendToServer(ctx, byteArrayFactory(MessageType.SET_USERNAME, singleContent)); 
	}
	
	
	
	// mainLoop - 대기실 화면
	public static void mainLoop(ChannelHandlerContext ctx) {
		pool.execute(new Runnable() {
			@Override
			public void run() {
				boolean isShutdown = false;
				do {
					if(inRoom) {
						break;
					}
					System.out.println();
					System.out.print("==============================================================================================\r\n");
					System.out.print("----------------------------------------- NCHAT 대기실 -----------------------------------------\r\n");
					System.out.print("==============================================================================================\r\n");
					System.out.println("1) 방 참여하기");
					System.out.println("2) 방 만들기");
					System.out.println("3) 전체 사용자 목록 보기");
					System.out.println("4) 귓속말 보내기");
					System.out.println("5) 본인 정보 확인");
					System.out.println("0) 종료하기");
					String line = lineFactory();
					switch(line) {
						case "1" : sendToServer(ctx, byteArrayFactory(MessageType.GET_ROOMLIST)); isShutdown = true; break;
						case "2" : sendToServer(ctx, byteArrayFactory(MessageType.CREATE_ROOM_CONFIRMATION)); isShutdown = true; break;
						case "3" : sendToServer(ctx, byteArrayFactory(MessageType.GET_USERLIST)); isShutdown = true ; break;
						case "4" : privateMessage(ctx); break;
						case "5" : accountInfo(ctx); break;
						case "0" : programExitRequest(ctx); isShutdown = true;
						case "y" : if(invitationReceived) { sendToServer(ctx,  byteArrayFactory(MessageType.ENTER_ROOM, globalMasterName)); isShutdown = true; break; }
						default : continue;
						}
				}while(!isShutdown);
			}
		});
	}
	
	// RoomLoop - 방화면
	public static void RoomLoop(ChannelHandlerContext ctx) {
		invitationReceived = false;
		System.out.println();
		System.out.print("==============================================================================================\r\n");
		System.out.print("--------------------------------------------- 채팅방 입장 ----------------------------------------\r\n");
		System.out.print("==============================================================================================\r\n");
		System.out.println("명령어 종류 \"/help\"");
			pool.execute(new Runnable() {
				@Override
				public void run() {
					boolean isExit = false;
					do {
						if(!inRoom) { 
							break ;
						};
						String line= lineFactory();
						if(line.equals("/help") || line.equals("/room") || line.equals("/exit") || line.equals("/private")
								|| (roomInfo.getRoomMaster().equals(userInfo.getUserName()))&&line.equals("/invite") 
								|| (roomInfo.getRoomMaster().equals(userInfo.getUserName()))&&line.equals("/kickout")
								|| (roomInfo.getRoomMaster().equals(userInfo.getUserName()))&&line.equals("/name")
								|| (roomInfo.getRoomMaster().equals(userInfo.getUserName()))&&line.equals("/password") ){
							switch(line) {
							case "/help" : if(roomInfo.getRoomMaster().equals(userInfo.getUserName())) {System.out.println("방 제목 바꾸기 \"/name\""); System.out.println("방 비번 바꾸기 \"/password\""); System.out.println("초대하기 \"/invite\""); System.out.println("강퇴하기 \"/kickout\""); }; System.out.println("귓속말 보내기 \"/private\""); System.out.println("방 정보 및 참여자 보기 \"/room\""); System.out.println("방 나가기 \"/exit\""); break;
							case "/name" : changeRoomInfo(ctx, "name"); break;
							case "/password" : changeRoomInfo(ctx, "password") ;break;
							case "/private" : privateMessage(ctx); break;
							case "/room" : getRoomInfo(ctx); break;
							case "/exit" :  sendToServer(ctx,  byteArrayFactory(MessageType.EXIT_ROOM)); isExit = true; break;
							case "/invite" :  sendInvitation(ctx); break;
							case "/kickout" :  kickoutUser(ctx); break;
							default : break;
							}		
						}else {
							//위의 명령어 제외한 모든 메시지는 채팅으로 공유 
							ctx.writeAndFlush(byteArrayFactory(MessageType.CHAT_MESSAGE, line));
						}
					}while(!isExit);
				}
			});	
	}
	
	
	// welcomeScreen method - 유저 닉네임 입력 화면
	private static String setUserName() {
		boolean isSet = false;
		String content = null;
		do {
			try {
				System.out.print("NCHAT에 오신것을 환영합니다. 시작 전, 닉네임을 먼저 설정 해주세요. (2자~10자) : \r\n");
				content = inputValidation("validateUserName");
	            if (content!=null) {
					return content;
				}else {
					System.out.print("닉네임을 형식에 맞게 입력해 주시기 바랍니다. \r\n");
//					제대로 된 형식의 닉네임을 입력할때 까지 무한 반복
					continue;
				}					
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		} while (!isSet);	
		return content;
	}	
    
   
   // mainLoop method - 방 생성 화면
   public static boolean createRoomRequest(ChannelHandlerContext ctx) {
		boolean isExit = false, isSet = false; doNotDisturb = true;
		do {
			System.out.println("뒤로가기  \"/exit\"");
			System.out.println("방 제목 입력 (2자 ~ 20자) : ");
			String roomName = inputValidation("receiveRoomName");
//			boolean isinput = false;
//			length // room name
			if(roomName == null) {
				System.out.println("형식에 맞게 입력해 주세요.");
//				continue;
			}
//			"/exit".equals(roomName)
			if(roomName.equals("/exit")) {
				isExit = true;
			}else {
				System.out.println("뒤로가기  \"/exit\"");
				System.out.println("방 비밀번호 입력 (2자~10자, 없으면 빈칸 입력) : ");
				String roomPassword = inputValidation("receiveRoomPassword");
				if(roomPassword == null) {
					System.out.println("형식에 맞게 입력해 주세요. 방 제목 입력 화면으로 돌아갑니다.");
					System.out.println();
					continue;
				}
				if(roomPassword.equals("/exit")) {
					continue;
				}else {
					if(roomName!=null && roomPassword!= null) {
						// send to Server 
						sendToServer(ctx, byteArrayFactory(MessageType.CREATE_ROOM, roomName, roomPassword));
						isSet = true;
						break;
					}					
				}
			}					
		}while(!isExit);
		return isSet;
   }
   
   // mainLoop method - 방 입장 전 방 목록 화면
   public static void selectRoomFromList(ChannelHandlerContext ctx, List <String[]> roomList) {
   	boolean isSelected = false;
   	do {
   		System.out.println("뒤로가기  \"/exit\"");
   		System.out.println("입장하실 채팅방을 선택하여 주시기 바랍니다.");
   		System.out.print("------------------------------------------- 활성화 된 방 목록 -------------------------------------\r\n");
   		for(String[] allRooms : roomList) {
   			System.out.println((roomList.indexOf(allRooms) + 1) +") "+
   		" 방 제목 [ " + allRooms[0] + " ] 방 비밀번호 [ " + allRooms[3] +
   		" ] 방장 [ " + allRooms[2] + " ] 참여 인원 [ " + allRooms[4] + " ] ");
   		}
   		System.out.print("----------------------------------------------------------------------------------------------\r\n");

   		int option;
   			String line = lineFactory();
   		try {
   			if(line.equals("/exit")) { mainLoop(ctx); break;};
				option = Integer.parseInt(line);
			}catch (NumberFormatException e) {
				System.out.println("숫자만 입력해 주시기 바랍니다.");
				continue;
   		}
   		if(option > 0 && option <= roomList.size()) {
   			// 선택 한 옵션과 방의 index number 가 일치. 해당 방으로 입장한다.
   			if(!roomList.get(option-1)[1].equals("")) {
   				// 방에 비밀번호가 걸려있다면 비밀번호 확인을 한다.
   				boolean checkRoomPassword = checkRoomPassword(roomList.get(option-1)[1]);
   				if(!checkRoomPassword) {
   					continue;
   				}
   			}
   			// enterRoom 처리 	
   			sendToServer(ctx, byteArrayFactory(MessageType.ENTER_ROOM, roomList.get(option-1)[2]));
   			isSelected = true;
   		}else {
   			System.out.println("제공된 옵션안에서 선택 해 주시기 바랍니다.");
				continue;
   		}
   	}while(!isSelected);
   }
   
   
   // mainLoop method - 모든 유저 목록 화면
   public static void printAllUsers(ChannelHandlerContext ctx, List<String>userList) {
	   	int index = 1;
			System.out.print("------------------------------------- 현재 활동중인 유저 목록 ---------------------------------------\r\n");
	   	if(userList != null) {
	   		for(String userName : userList) {
	   			System.out.println(index + ") " + userName);
	   			index ++;
	   		}
	   		System.out.print("----------------------------------------------------------------------------------------------\r\n");
	   	}
	}
   
   
   // mainLoop method - 귓속말 보내기 
   public static void privateMessage(ChannelHandlerContext ctx) {
	   String nickName;
	   String content;
	   boolean isNameSet = false;
	   boolean isContentSet = false;
	   do {
	   		System.out.println("뒤로가기  \"/exit\"");
	   		System.out.println("귓속말을 보낼 유저 이름을 입력하세요 : (2자 ~ 10자)");
	   		nickName = inputValidation("validateUserName");
	   		if (nickName == null) {
        	   System.out.print("닉네임을 형식에 맞게 입력해 주시기 바랍니다. \r\n");
//				제대로 된 형식의 닉네임을 입력할때 까지 무한 반복
        	   continue;
			}
	   		if(nickName.equals("/exit")) {
	   			break;
	   		}
	   		if(nickName.equals(userInfo.getUserName())){
	   			System.out.println("본인에게 귓속말을 보낼 수 없습니다.");
	   			continue;
	   		}
	   		do {
		   		System.out.println("뒤로가기  \"/exit\"");
	   			System.out.println("전달할 귓속말을 입력하세요 : ");
	   			content = lineFactory();
	   			if(content == null || content == "") {
	   				System.out.println("귓속말 내용을 입력해 주시기 바랍니다.");	
	   				continue;
	   			}
	   			else if(content.equals("/exit")) {
	   				break;
	   			}
	   			else {	   			
	   	   			sendToServer(ctx, byteArrayFactory(MessageType.PRIVATE_MESSAGE, nickName, content));
	   	   			isContentSet = true;
	   	   			isNameSet = true;
	   			}
	   		}while(!isContentSet);
	   }while(!isNameSet);
   }
   
   
   // mainLoop method - 본인 정보 확인
   private static void accountInfo(ChannelHandlerContext ctx) {
		System.out.print("------------------------------------------- 나의 정보 -------------------------------------------\r\n");
    	System.out.println("나의 닉네임 : " + userInfo.getUserName());
    	System.out.println("나의 아이피 & 포트 주소: " + ctx.channel().remoteAddress());
		System.out.print("----------------------------------------------------------------------------------------------\r\n");
    }
   
   
   // mainLoop method - 클라이언트 프로그램 종료
   private static void programExitRequest(ChannelHandlerContext ctx) {
		   try {
			ctx.close().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   }
   
   
   // roomLoop method - 방 비밀번호 확인 화면
   private static boolean checkRoomPassword(String password) {
   	// String password 를 서버에서 클라이언트보내지 말고 ( 비밀번호 안전 보장을 위해? ) 
   	// password 유무 boolean 값을 받아서 값이 있을 시 서버로 재 접근해서 비밀번호 확인 로직을 처리하는 식으로 하자 
   	boolean isExit = false;
   	boolean check = false;
   	String line="";
   	do {
   		System.out.println("뒤로가기  \"/exit\"");
   		System.out.println("비밀번호 입력 : ");
   		line = lineFactory();
			if(line.equals(password)) {
				isExit = true;
				break;
			}
			if(line.equals("/exit")) {
				break;
			}
			else {
				System.out.println("비밀번호가 일치하지 않습니다.");
				continue;
			}
   	}while(!check);
   return isExit;
   }
   
   
   // roomLoop method - 방 제목 or 방 비밀번호 바꾸기 
   private static void changeRoomInfo(ChannelHandlerContext ctx, String type) {
	   String roomName = null;
	   String roomPassword = null;
	   boolean isExit = false;
	   do{
		   if(type.equals("name")) {
			   	System.out.println("뒤로가기  \"/exit\"");
				System.out.println("변경 할 방 제목 입력 (2자 ~ 20자) : ");
				roomName = inputValidation("receiveRoomName");
				if(roomName == null) {
					System.out.println("형식에 맞게 입력해 주세요.");
					continue;
				}
				if(roomName.equals("/exit")) {
					isExit = true;
				}
		   }
		   if(type.equals("password")) {
			   	System.out.println("뒤로가기  \"/exit\"");
				System.out.println("방 비밀번호 입력 (2자~10자, 없으면 빈칸 입력) : ");
				roomPassword = inputValidation("receiveRoomPassword");
				if(roomPassword == null) {
					System.out.println("형식에 맞게 입력해 주세요.");
					continue;
				}
				if(roomPassword.equals("/exit")) {
					isExit = true;
				}
		   }		   
		   if(roomName!=null && roomPassword == null) {
				sendToServer(ctx, byteArrayFactory(MessageType.CHANGE_ROOMNAME, roomName));
			   isExit = true;
		   }else {
				sendToServer(ctx, byteArrayFactory(MessageType.CHANGE_ROOMPASSWORD, roomPassword));
			   isExit = true;
		   }
	   }while(!isExit);
   }
   
   
   // roomLoop method - 초대장 보내기 화면 
   private static void sendInvitation(ChannelHandlerContext ctx) {
   		boolean isExit = false;
		do {
			System.out.println("초대 하실 닉네임을 입력하세요 (2자 ~ 10자)");
			String userName = inputValidation("validateUserName");
			if(userName != null) {
				sendToServer(ctx, byteArrayFactory(MessageType.INVITE_USER, userName));
				isExit = true;
			}else {
				System.out.println("닉네임을 형식에 맞게 입력해 주시기 바랍니다.");
				continue;	    		
			}    		
		}while(!isExit);
   }
   
   
   // roomLoop method - 강퇴하기 화면 
   private static void kickoutUser(ChannelHandlerContext ctx) {
	   boolean isExit = false;
	   do {
		   System.out.println("강퇴 하실 닉네임을 입력하세요 (2자 ~ 10자)");
			String userName = inputValidation("validateUserName");
			if(userName.equals(userInfo.getUserName())){
				System.out.println("자신을 강퇴 할 수 없습니다.");
				isExit = true;
			}else if(userName != null && !userName.equals(userInfo.getUserName())) {
				sendToServer(ctx, byteArrayFactory(MessageType.KICKOUT_USER, userName));
				isExit = true;
			}else {
				System.out.println("닉네임을 형식에 맞게 입력해 주시기 바랍니다.");
				continue;	    		
			}  
	   }while(!isExit);
   }
   
   
   
   // roomLoop method - 서버에 방 정보 request 보내기
   private static void getRoomInfo(ChannelHandlerContext ctx){
   	sendToServer(ctx, byteArrayFactory(MessageType.GET_ROOMINFO, roomInfo.getRoomMaster()));
   }

   
   
   // common method - 입력값 받기 
   private static String lineFactory() {
   	String line = null;
   	try {
   		line = br.readLine();					
   	} catch (IOException e) {
   		e.printStackTrace();
   	} catch (Exception e1) {
   		e1.printStackTrace();
   	}
   	return line;
   }
   
   // common method - validation checking logic 
   private static String inputValidation(String type) {
   	// validateUserName, roomName, roomPassword
	   	do {
	   		String line = lineFactory();
				if (line == null) {
	//               	 빈칸이면 안됨.
					 return null;
	  			}
				line.trim();
				if(type.equals("receiveRoomName") ) {
		            if(line.length()<2 || line.length()>20) {
	//   	            	2자 ~ 20자 사이만 가능.
		            	return null;
		            }
				}
				if(type.equals("validateUserName") 
						|| type.equals("receiveRoomPassword")) {
					line = removeMultipleSpaces(line);
		            if(line.split("\\s").length > 1) {
	//   	            	띄어쓰기가 있으면 안됨.
		            	return null;
		            }
				}
		        if(type.equals("validateUserName")) {
		            if(line.length()<2 || line.length()>10) {
	//   	            	2자 ~ 10자 사이만 가능.
		            	return null;
		            }
		        }
		        if(type.equals("receiveRoomPassword")) {
		        	if( ( line.length()<2 || line.length()>10 ) && (!line.equals(""))) {
		        		return null;		        		
		        	};
		        }
	   		return line;	
	   	}while(true);
   }
   
   
	// common method - 입력 값 띄어쓰기 확인 후 제거
   private static String removeMultipleSpaces(String line) {
   	StringTokenizer st = new StringTokenizer(line," ");
   	StringBuffer sb = new StringBuffer();
   	while(st.hasMoreElements()) {
   		sb.append(st.nextElement()).append(" ");
   	}
   	return sb.toString().trim();
   }

   
   // common method - send input message to server
   public static void sendToServer(ChannelHandlerContext ctx, byte [] packet) {
   	ChannelFuture future =  ctx.writeAndFlush(packet); // send
   	future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) { // I/O operation이 성공 했을 때.
					if(!(ctx.channel().pipeline().first() instanceof IdleStateHandler)) { 	// 특정 시간 동안 read, write, 혹은 두 동작이 없을 때 이를 체크해 사용자에게 알려주는 핸들러 이다.
						ctx.channel().pipeline().addFirst(new IdleStateHandler(3,0,0));  // 3 초동안 channel read 를 하지 못하면 이벤트를 발생 시키는 조건으로 핸들러를 파이프 라인 가장 처음에 추가함.						
						setToStorage(packet); // 패킷 임시 저장하기.
					}
				}else {
					System.out.println("서버에게 요청을 보내는데 실패했습니다. 다시한번 시도해 주시기 바랍니다.");
				}
			}
		});
   }
   
   
   

}
