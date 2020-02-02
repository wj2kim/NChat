package com.chat.netty.server;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.chat.netty.reference.Command;
import com.chat.netty.reference.FalseResponse;
import com.chat.netty.reference.SetUserName;
import com.chat.netty.reference.TrueResponse;
import com.chat.netty.vo.NChatManager;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<Command>{
	
	private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	private final AttributeKey <UserInfo> user = AttributeKey.valueOf("userInfo");
	
	private ExecutorService pool;
	private Runnable shutdownServer;
	private NChatManager manager = new NChatManager();
//	private UserManager userManager = new UserManager();
	
	
	
	public ServerHandler(ExecutorService pool, Runnable shutdownServer) {
		this.pool = pool;
		this.shutdownServer = shutdownServer;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Channel이 활성화 될때 마다 UserInfo와 연결짓기.
		ctx.channel().attr(user).set(new UserInfo(ctx));
		UserInfo userInfo = ctx.channel().attr(user).get();
		System.out.println("과연 : " + userInfo.getCtx());
		super.channelActive(ctx);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에 접속했습니다.");
		
		
//		for(Channel ch : channelGroup) {
//			ch.write(incomming.remoteAddress()+" 님이 입장했습니다.");
//		}
//		channelGroup.add(incomming);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 사용자가 ProgramExit 을 했을때 무조건 여기로 옴 
		
//		UserInfo userInfo = channelInfo.getUserInfo();
////	if(userInfo != null) {
////	ChannelInfo 에서 UserInfo를 가지고 와서 이벤트 처리 
////	UserManager.userExit(userinfo.getUserName());
////	channelInfo.setUserInfo(null);
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에서 나갔습니다.");
		
		
//		for(Channel ch : channelGroup) {
//			ch.write(incomming.remoteAddress()+" 님이 퇴장했습니다.");
//		}
//		channelGroup.remove(incomming);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("클라이언트로 부터 요청받음 : "+ cmd.toString());
		LOGGER.info("클라이언트로 부터 요청받음 : "+ cmd.getCommandType());
		LOGGER.info("클라이언트로 부터 요청받음 : "+ cmd.getContent());
		
		UserInfo userInfo = ctx.channel().attr(user).get();
		
		if(cmd instanceof SetUserName) {
			System.out.println("userInfo : "+userInfo.toString());
			System.out.println("userInfo : "+userInfo.getCtx());
			setUserName(userInfo, cmd);
		}
		
	}
	
		

//		ChannelInfo channelInfo = aCtx.channel().attr(ChannelListener.CHANNEL_INFO).get();
		
//		if(msg instanceof ProgramExitRequest) {
////			processExit(msg, channelInfo);
//			processExit(ctx);
//		}
		
		
//		if(msg instanceof UserInfo) {
//			LOGGER.info(((UserInfo) msg).getUserId()+ " : " +
//			((UserInfo) msg).getUserName());
//		}
//		
//		String message = null;
//		message = (String)msg;		
//		Channel incomming = ctx.channel();
//		LOGGER.info("주고 받은 메시지 : " + incomming.remoteAddress() + " : "+ message);
//		
//		if(msg instanceof SetUserName) {
//			boolean isUserNameTaken(msg);
//			if(isUserNameTaken) {
//				sendResponse(isUserNameTaken);
//			}
//		}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	private void setUserName(UserInfo userInfo, Command cmd) {
		ChannelHandlerContext ctx = userInfo.getCtx();
		if(isUserNameTaken(cmd)) {
			// 중복
			// 클라이언트에게 사용중이라 응답 보내기 
			cmd = new FalseResponse("nameAlreadyExist","");
		}else {
			// 서버에 유저 정보 저장하고 
			// 클라이언트에게 처리 완료 응답 보내기
			userInfo.setUserName(cmd.getContent());
			manager.getAllUsers().put(cmd.getContent(), userInfo);
			LOGGER.info("IP주소가 "+ctx.channel().remoteAddress()+"인 사용자가 닉네임을 등록했습니다. 닉네임 : " + cmd.getContent());
			cmd = new TrueResponse("nameSet","");
		}
		ctx.writeAndFlush(cmd);
	}
	
	private boolean isUserNameTaken(Command cmd) {
//		ChatManager을 이용해 닉네임 중복 체크
		if(manager.getAllUsers()==null || manager.getAllUsers().isEmpty()) {
//					! manager.getAllUsers().containsKey(cmd.getContent())) {
			return false;
		}
		return true;
	}
		
//		RoomInfo room = new RoomInfo();
//		room.setRoomId(1);
//		room.setRoomName("어서 오세요");
//		Map <String, RoomInfo> allRooms = new HashMap<String, RoomInfo>();
//		allRooms.put(room.getRoomName(), room);
//		manager.setAllRooms(allRooms);
//		
//		
//		return false;
//	}
	
//	private void sendToClient(ChannelHandlerContext ctx, Command cmd) {
//		ctx.writeAndFlush(cmd);
//	}


	
	
	
//	private void processExit(ChannelHandlerContext ctx) {
////		UserInfo userInfo = channelInfo.getUserInfo();
////		if(userInfo != null) {
////		ChannelInfo 에서 UserInfo를 가지고 와서 이벤트 처리 
////		UserManager.userExit(userinfo.getUserName());
////		channelInfo.setUserInfo(null);
//		LOGGER.info(ctx.channel().remoteAddress()+"의 사용자가 나갔습니다.");
////		}
//	}
	
	

}
