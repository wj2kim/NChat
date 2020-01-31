package com.chat.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ClientHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	private ExecutorService pool;
	
	public ClientHandler(ExecutorService pool) {
		this.pool = pool;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		mainLoop(ctx);
		super.handlerAdded(ctx);
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
	
	public void mainLoop(ChannelHandlerContext ctx) {
		boolean isExit = true;

		pool.execute(new Runnable() {
			@Override
			public void run() {
				do {
					System.out.println();
					System.out.print(" --------------------------------------------- \r\n");
					System.out.print(" ----------- NChat에 오신것을 환영합니다 ----------- \r\n");
					System.out.print(" --------------------------------------------- \r\n");
					System.out.println();
					System.out.println("닉네임을 입력하세요.");
					String content = setUserName();
					
					
				}while(!isExit);
			}
		});
	}
	
    private String setUserName() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
        	line = br.readLine();
            if (line == null) {
            	// 빈칸이면 안됨.
                return null;
            }
            if(line.split("\\s").length > 1) {
            	// 띄어쓰기가 있으면 안됨.
            	return null;
            }
            if(line.length()<2 || line.length()>10) {
//            	2자 ~ 10자 사이만 가능.
            	return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return line;
    }
    

	
	
}
