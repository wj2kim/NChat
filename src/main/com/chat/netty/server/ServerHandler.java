package com.chat.netty.server;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.chat.netty.vo.ChannelInfo;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	public static final AttributeKey<ChannelInfo> CHANNEL_INFO = AttributeKey.valueOf("channelInfo");
	
	private ExecutorService pool;
	private Runnable shutdownAction;
	
	
	public ServerHandler(ExecutorService pool, Runnable shutdownAction) {
		this.pool = pool;
		this.shutdownAction = shutdownAction;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Channel이 활성화 될때 마다 ChannelInfo와 연결짓기.
		ChannelPipeline pipeline = ctx.pipeline();
		ChannelHandlerContext context = null;
        for(Iterator<Entry<String, ChannelHandler>>  iter = pipeline.iterator();iter.hasNext();) {
            Entry<String, ChannelHandler> entry = iter.next();
            context = pipeline.context(entry.getValue());
        }
        ctx.channel().attr(CHANNEL_INFO).set(new ChannelInfo(context));
		super.channelActive(ctx);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에 접속했습니다.");
		
		
		for(Channel ch : channelGroup) {
			ch.write(incomming.remoteAddress()+" 님이 입장했습니다.");
		}
		channelGroup.add(incomming);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에서 나갔습니다.");
		
		
		for(Channel ch : channelGroup) {
			ch.write(incomming.remoteAddress()+" 님이 퇴장했습니다.");
		}
		channelGroup.remove(incomming);
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if(msg instanceof UserInfo) {
			LOGGER.info(((UserInfo) msg).getUserId()+ " : " +
			((UserInfo) msg).getUserName());
		}
		
		String message = null;
		message = (String)msg;		
		Channel incomming = ctx.channel();
		LOGGER.info("주고 받은 메시지 : " + incomming.remoteAddress() + " : "+ message);
		
//		for(Channel ch : channelGroup) {
//			if(ch != incomming) {
//				ch.writeAndFlush("FROM - "+incomming.remoteAddress()+" : "+message+"\r\n");
//			}
//			if("bye".equals(message.toLowerCase())) {
//				ctx.close();
//			}
//		}
//		channelGroup.remove(incomming);

		
		
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
