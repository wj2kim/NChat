package com.chat.netty.server;

import static com.chat.netty.server.RequestListener.manager;
import static com.chat.netty.server.RequestListener.responseFactory;
import static com.chat.netty.server.RequestListener.user;

import org.apache.log4j.Logger;

import com.chat.netty.reference.PacketFactory;
import com.chat.netty.reference.PacketIntegration;
import com.chat.netty.reference.TemporaryPacketStorage;
import com.chat.netty.vo.RoomInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class ServerHandler extends SimpleChannelInboundHandler<byte[]>{
	
	private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());

	PacketIntegration integration = new PacketIntegration();
	TemporaryPacketStorage storage = new TemporaryPacketStorage();
	private String userName="";
	
	public ServerHandler() {
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Channel이 활성화 될때 마다 UserInfo 초기화 해서 세팅해주기
		ctx.channel().attr(user).set(new UserInfo(ctx));
		
		// broadcast 용. 해당 채널 globalChannel 에 등록
		manager.getGlobalChannel().add(ctx.channel());
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] - 서버에 접속 ");
	}
	
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		String userName = ctx.channel().attr(user).get().getUserName();
		RoomInfo roomInfo = (RoomInfo) ctx.channel().attr(AttributeKey.valueOf("roomInfo")).get();
		if(roomInfo!=null && roomInfo.getRoomMaster().equals(userName)) { // 방장일 경우
			manager.removeRoom(roomInfo);			
		}
		if(roomInfo!=null && roomInfo.getUsersInRoom().containsKey(userName)){ // 방에 있는 일반 유저라면 
			roomInfo.getUsersInRoom().remove(userName);
		}
		integration.remove(ctx.channel().id()); // integration 객체안에 있는 map에 값이 있다면 remove

		// 매니저 객체에서 나가는 유저 정보 제거하기
		manager.removeUser(userName);
		// globalChannel 에서도 제거하기
		manager.getGlobalChannel().remove(ctx.channel());
		LOGGER.info("IP:포트 주소 [ " + ctx.channel().remoteAddress()+" ] 닉네임 [ " + userName + " ] - 서버와의 연결 해제 ");
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte [] packet) throws Exception {
		long startTime = System.nanoTime();  // read가 시작되고 시간 counting 시작
		ChannelId id = ctx.channel().id(); // 채널의 고유 아이디
		
//		System.out.println("받은 패킷 사이즈 : " + packet.length);
		if(integration.isExist(id)) { // motherPacket 이 세팅 되어있는 packet 은 이쪽으로.
			integration.add(id, packet);
			if(integration.isIntegrated(id)) { // 패킷이 완전하게 합쳐 졌다면
				responseFactory(ctx, integration.getIntegratedPacket(id), startTime); // 통합 됬으면 next step 으로 가기.
				return;
			}
			return;
		}
		PacketFactory factory = new PacketFactory(packet);
		int packetSize = factory.getSize(); // 내부적으로 getShort 메소드를 이용해 2 byte 크기의 패킷 전체 길이 정보를 가져온다.
		int packetId = factory.getId();
		byte messageType = factory.getMessageType();
		
//		System.out.println("받아야하는 패킷 사이즈 : " + packetSize);
		if(packetSize != packet.length) { // 받아야할 패킷 사이즈와 받은 패킷 사이즈를 비교
			integration.setMotherPacket(id, packet, packetSize); //  없다면 해당 packet이 motherPacket
			return;
		}
		
		if(!(messageType == 19)) {  // store every messageType but chat message.
			TemporaryPacketStorage.checkIdandStore(packetId, packet);
		}
		responseFactory(ctx, packet, startTime); // 완전하게 받은 패킷 바로 처리.
	}
	
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("IP:포트 주소 [" +ctx.channel().remoteAddress() + " ] 닉네임 [ " + userName + " ] ServerHandler exception 에러", cause);
		ctx.close();
	}


}
