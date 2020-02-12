package com.chat.netty.codec;

import java.util.List;

import com.chat.netty.reference.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class CommandCodec extends MessageToMessageCodec<Message, Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(ctx);
		System.out.println(msg);
		System.out.println(out);
		
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(ctx);
		System.out.println(msg);
		System.out.println(out);
		
	}
	



}
