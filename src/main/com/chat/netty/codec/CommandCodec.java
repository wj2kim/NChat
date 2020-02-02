package com.chat.netty.codec;

import java.util.List;

import com.chat.netty.reference.Message;
import com.chat.netty.reference.MessageType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class CommandCodec extends ByteToMessageCodec<Message>{
	
	protected void encode(ChannelHandlerContext ctx, Message cmd, ByteBuf out) throws Exception {
		MessageType command = cmd.getCommandType();
		
		
	};
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
