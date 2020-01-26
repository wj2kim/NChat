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
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NChatServer {
	
	private static final Logger LOGGER = Logger.getLogger(NChatServer.class.getName());
	private static final int PORT = 8080;
	
	ServerBootstrap bStrap=new ServerBootstrap();
	EventLoopGroup bossGroup=new NioEventLoopGroup();
	
	public NChatServer() {
		bStrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		ChannelInitializer<Channel>initializer = new ChannelInitializer<Channel>() {
			// 채팅 서버의 채널을 초기화
			@Override
			protected void initChannel(Channel ch) throws Exception {
				// TODO Auto-generated method stub
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new NChatServerHandler());
			}
		};
//		EventLoopGroup bossGroup = new NioEventLoopGroup();
//		serverOff();
		bStrap.group(bossGroup).channel(NioServerSocketChannel.class).childHandler(initializer);
	}
	
	public void run() {
		ChannelFuture future = bStrap.bind(new InetSocketAddress(PORT));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					LOGGER.info("서버를 시작합니다.");
				}else {
					LOGGER.error("서버를 실행하지 못했습니다. 다시 한번 시도해 주시기 바랍니다.",future.cause());
				}
			}
		});
		try {
			future.channel().closeFuture().sync();
		}catch(InterruptedException e) {
			LOGGER.error("InterruptedException msg", e);
			// 에러의 추적성을 높이기 위해 전체 에러 스택을 다 넘기는 편이 좋다고 한다.
		}
	}
	
	public void serverOff() {
		try {
			bossGroup.shutdownGracefully().sync();
		}catch(InterruptedException e) {
			LOGGER.error("서버를 종료하는 도중 에러가 발생했습니다.", e);
		}
	}
	
	public static void main (String[] args) {
		NChatServer server = new NChatServer();
		server.run();
	}
}
