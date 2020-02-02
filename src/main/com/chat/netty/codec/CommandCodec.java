package com.chat.netty.codec;

import java.util.List;

import com.chat.netty.reference.Command;
import com.chat.netty.reference.CommandType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class CommandCodec extends ByteToMessageCodec<Command>{
	
	protected void encode(ChannelHandlerContext ctx, Command cmd, ByteBuf out) throws Exception {
		CommandType command = cmd.getCommandType();
		
		
	};
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
