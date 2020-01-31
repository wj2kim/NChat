package com.chat.netty.server;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.chat.netty.vo.UserInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	private ExecutorService pool;
	private Runnable shutdownServer;
	
	
	public ServerHandler(ExecutorService pool, Runnable shutdownServer) {
		this.pool = pool;
		this.shutdownServer = shutdownServer;
	}
	
//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		// Channel이 활성화 될때 마다 ChannelInfo와 연결짓기.
//		ChannelPipeline pipeline = ctx.pipeline();
//		ChannelHandlerContext context = null;
//        for(Iterator<Entry<String, ChannelHandler>>  iter = pipeline.iterator();iter.hasNext();) {
//            Entry<String, ChannelHandler> entry = iter.next();
//            context = pipeline.context(entry.getValue());
//        }
//       
//		super.channelActive(ctx);
//	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에 접속했습니다.");
		
		
//		for(Channel ch : channelGroup) {
//			ch.write(incomming.remoteAddress()+" 님이 입장했습니다.");
//		}
//		channelGroup.add(incomming);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incomming = ctx.channel();
		LOGGER.info("IP주소가 " + incomming.remoteAddress()+" 인 사용자가 서버에서 나갔습니다.");
		
		
//		for(Channel ch : channelGroup) {
//			ch.write(incomming.remoteAddress()+" 님이 퇴장했습니다.");
//		}
//		channelGroup.remove(incomming);
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
//		
//		if(msg instanceof SetUserName) {
//			boolean isUserNameTaken(msg);
//			if(isUserNameTaken) {
//				sendResponse(isUserNameTaken);
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
	
	private boolean isUserNameTaken(Object msg) {
		// ChatManager을 이용해 닉네임 중복 체크
//		if(manager.getAllUsers().isEmpty() || !manager.getAllUsers.containsKey(msg)) {
//			return true;
//		}
		return false;
	}
	
	private void sendResponse() {
		
	}
	
	

}
