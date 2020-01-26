package com.chat.netty.server;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NChatServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(NChatServerHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		LOGGER.info("새로운 사용자가 입장했습니다.");
		ctx.writeAndFlush("NChat에 오신것을 환영합니다.");
//		System.out.println("채널 그룹 사이즈 : "+channelGroup.size());
//		System.out.println("채널 그룹 : "+channelGroup);
		
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel newChannel = ctx.channel();
		LOGGER.info(newChannel.remoteAddress()+" 사용자가 서버에 접속했습니다.");
		for(Channel ch : channelGroup) {
			ch.writeAndFlush(newChannel.remoteAddress()+" 이 입장했습니다.(서버에 handler added)\r\n");
		}
		channelGroup.add(newChannel);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("사용자가 서버에서 나갔습니다. ");
		Channel newChannel = ctx.channel();
		for(Channel ch : channelGroup) {
			ch.writeAndFlush(newChannel.remoteAddress()+" 가 연결을 끊었습니다. (서버에서 handler removed)\r\n");
		}
		channelGroup.remove(newChannel);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = (String)msg;
		LOGGER.info("전송 된 메세지 :"+message);
		Channel incomming = ctx.channel();
		for(Channel ch : channelGroup) {
			if(ch != incomming) {
				ch.writeAndFlush(incomming.remoteAddress() + " : " + message+"/r/n");
			}
		}
	}  
	
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		ctx.flush();
//	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	

}
