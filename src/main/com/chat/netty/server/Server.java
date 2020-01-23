package com.chat.netty.server;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
//	private static final int PORT = 8023;
	ServerBootstrap bStrap=new ServerBootstrap();
	
	public Server() {
		bStrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		ChannelInitializer<Channel>initializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				// TODO Auto-generated method stub
				ch.pipeline().addLast();
				ch.pipeline().addLast();
				ch.pipeline().addLast();
				ch.pipeline().addLast();
				ch.pipeline().addLast();
			}
		};
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		serverOff();
		bStrap.group(bossGroup).channel(NioServerSocketChannel.class).childHandler(initializer);
	}
	
	public void run() {
		ChannelFuture future = bStrap.bind(new InetSocketAddress(8080));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.info("서버를 시작합니다.");
				}else {
					logger.info("서버 작동에 실패했습니다. 다시 한번 시도해 주시기 바랍니다.");
					logger.error("exception msg",future.cause());
				}
			}
		});
		try {
			future.channel().closeFuture().sync();
		}catch(InterruptedException e) {
			logger.error("exception msg", e);
			// 에러의 추적성을 높이기 위해 전체 에러 스택을 다 넘기는 편이 좋다고 한다.
		}
	}
	
	public void serverOff() {
		
	}
	
	public static void main (String[] args) {
		Server server = new Server();
		server.run();
	}
}
