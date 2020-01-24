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
		LOGGER.info("새로운 사용자가 입장했습니다.");
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("사용자가 서버에 접속함");
		Channel newChannel = ctx.channel();
		for(Channel ch : channelGroup) {
			ch.write("서버에 채널 추가: "+newChannel.remoteAddress()+" 이 입장했습니다.\r\n");
		}
		channelGroup.add(newChannel);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("사용자가 서버에서 나갔습니다. ");
		Channel newChannel = ctx.channel();
		for(Channel ch : channelGroup) {
			ch.write("서버에서 채널 제외: "+newChannel.remoteAddress()+" 가 연결을 끊었습니다.\r\n");
		}
		channelGroup.remove(newChannel);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	}  
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	

}
