package com.chat.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	private static final int PORT = 8888;
	private Bootstrap boot = new Bootstrap();
	private NioEventLoopGroup workerLoopGroup = new NioEventLoopGroup();
	
	public Client() {
		ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(3000, Delimiters.lineDelimiter()));
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new ClientHandler());
			}
		};
		boot.group(workerLoopGroup).channel(NioSocketChannel.class).handler(initializer);
	}
	
	public void start() {
		ChannelFuture future = boot.connect(new InetSocketAddress("127.0.0.1", PORT));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					LOGGER.info("서버와의 연결에 성공하였습니다.");
					toChatting();
				}else {
					LOGGER.error("서버와의 연결에 실패했습니다. 다시 한번 시도해 주시기 바랍니다.",future.cause());
				}
			}
		});
		
		
		ChannelFuture lastWriteFuture = null;
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
	        	try {
	        		String line = in.readLine();
	        		if (line == null) {
	        			break;
	        		}
	        		
	        		// Sends the received line to the server.
	        		lastWriteFuture = future.channel().writeAndFlush(line + "\r\n");
	        		
	        		// If user typed the 'bye' command, wait until the server closes
	        		// the connection.
	        		if ("bye".equals(line.toLowerCase())) {
	        			future.channel().closeFuture().sync();
	        			break;
	        			
	        			// Wait until all messages are flushed before closing the channel.
	        		}
	        	}catch(InterruptedException e) {
	        		e.printStackTrace();
	        	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
		if (lastWriteFuture != null) {
			try {
				lastWriteFuture.sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
		try {
			future.channel().closeFuture().sync();
			LOGGER.info("서버와의 연결을 종료합니다.");
			shutdown();
		}catch(InterruptedException e) {
			LOGGER.error("InterruptedException msg", e);
		}
	}
	
	public void shutdown() {
		try {
			workerLoopGroup.shutdownGracefully().sync();
		}catch(InterruptedException e) {
			LOGGER.error("InterruptedException msg",e);
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	
	private void toChatting() {
		// NChat의 시작 루프. 닉네임 먼저 설정하기 -> 대기실
		
	}
	
	

}
