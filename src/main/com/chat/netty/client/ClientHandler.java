package com.chat.netty.client;

import static com.chat.netty.client.ResponseListener.printFactory;
import static com.chat.netty.view.ClientView.welcomeScreen;
import static com.chat.netty.reference.DisposablePacketStorage.*;

import com.chat.netty.reference.PacketFactory;
import com.chat.netty.reference.PacketIntegration;
import com.chat.netty.view.ClientView;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientHandler extends SimpleChannelInboundHandler<byte []>{
	PacketIntegration integration = new PacketIntegration();

	
	public ClientHandler() {
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		welcomeScreen(ctx);
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte [] packet) throws Exception {
		if(ctx.channel().pipeline().first() instanceof IdleStateHandler) { // 파이프라인에 등록된 첫번째 핸들러가 IdleStateHanler 인지 확인.
			ctx.channel().pipeline().removeFirst(); // 시간 내에 응답이 잘 도착했다면 파이프 라인 가장 처음에 추가한 핸들러를 remove 해준다.
			removeFromStorage();
		}
		
		ChannelId id = ctx.channel().id(); // 채널의 고유 아이디
		
		if(integration.isExist(id)) { // motherPacket 이 세팅 되어있는 packet 은 이쪽으로.
			integration.add(id, packet);
			if(integration.isIntegrated(id)) { // 패킷이 완전하게 합쳐 졌다면
				printFactory(ctx, packet); // 통합 됬으면 next step 으로 가기.
				return;
			}
			return;
		}
		
		PacketFactory factory = new PacketFactory(packet);
		int packetSize = factory.getSize(); // 내부적으로 getShort 메소드를 이용해 2 byte 크기의 패킷 전체 길이 정보를 가져온다.
		if(packetSize != packet.length) {  // 받아야할 패킷 사이즈와 받은 패킷 사이즈를 비교
			integration.setMotherPacket(id, packet, packetSize); //  없다면 해당 packet이 motherPacket
			return;
		}
		printFactory(ctx, packet); // 완전하게 받은 패킷 바로 처리.
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent)evt;
			if(e.state() == IdleState.READER_IDLE) {
				// 3초뒤에도 서버로 부터 응답이 없다면 데이터를 재전송 한다. x 3번 까지
				IdleStateCount(); // 카운트 다운 시작 +1 씩, 3번까지.
				if(isMaxIdleStateCount()) {
					System.out.println("서버에 오류가 생겼습니다. 다시 접속해 주시기 바랍니다.");  
					ctx.close(); // 3번의 재전송 이후에도 응답 처리가 되지 않으면 연결을 끊는다. 
				}else {
					byte [] packet = getFromStorage(); // 임시 저장한 패킷을 가지고 온다. 	
					if(packet != null ) {
						ClientView.sendToServer(ctx, packet); // 재전송 						
						System.out.println("서버에서 요청을 처리 중입니다...");
					}
				}
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			ctx.close();			
	}
	
	
	
}
