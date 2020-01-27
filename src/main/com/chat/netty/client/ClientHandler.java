package com.chat.netty.client;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.chat.netty.vo.ChannelInfo;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ClientHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(msg.toString());
		
//		for(Channel ch : channelGroup) {
//			
//		}
//		
//		ByteBuf readMessage = (ByteBuf)msg;
//		System.out.println("channelRead : "+readMessage.toString(Charset.defaultCharset()));
//		for(Channel ch : channelGroup) {
//			ch.writeAndFlush((String)msg);
//		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
}
