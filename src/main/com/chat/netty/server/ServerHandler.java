package com.chat.netty.server;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush("****** NChat에 오신것을 환영합니다. ****** \r\n");
		
//		System.out.println("채널 그룹 사이즈 : "+channelGroup.size());
//		System.out.println("채널 그룹 : "+channelGroup);
		
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel newChannel = ctx.channel();
		LOGGER.info("IP주소가 " + newChannel.remoteAddress()+" 인 사용자가 서버에 접속했습니다.");
		
		
		for(Channel ch : channelGroup) {
			ch.write(newChannel.remoteAddress()+" 님이 입장했습니다.");
		}
		channelGroup.add(newChannel);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel newChannel = ctx.channel();
		LOGGER.info("IP주소가 " + newChannel.remoteAddress()+" 인 사용자가 서버에서 나갔습니다.");
		
		
		for(Channel ch : channelGroup) {
			ch.write(newChannel.remoteAddress()+" 님이 퇴장했습니다.");
		}
		channelGroup.remove(newChannel);
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = null;
		message = (String)msg;		
		Channel newChannel = ctx.channel();
		LOGGER.info("주고 받은 메시지 : " + newChannel.remoteAddress() + " : "+ message);
		
//		for(Channel ch : channelGroup) {
//			if(ch != newChannel) {
//				ch.writeAndFlush("FROM - "+newChannel.remoteAddress()+" : "+message+"\r\n");
//			}
//			if("bye".equals(message.toLowerCase())) {
//				ctx.close();
//			}
//		}
//		channelGroup.remove(newChannel);

		
		
//		ByteBuf readMessage = (ByteBuf)msg;
//		ctx.writeAndFlush(readMessage);
		
//		String message = (String)msg;
//		LOGGER.info("전송 된 메세지 :"+message);
//		Channel incomming = ctx.channel();
//		for(Channel ch : channelGroup) {
//			if(ch != incomming) {
//				ch.writeAndFlush(incomming.remoteAddress() + " : " + message+"/r/n");
//			}
//		}
	}  
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	

}
