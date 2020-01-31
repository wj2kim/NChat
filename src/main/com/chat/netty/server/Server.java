package com.chat.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	private static final int PORT = 8888;
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	
	private final ExecutorService pool = Executors.newCachedThreadPool();
	private Runnable shutdownServer;
	
	ServerBootstrap sBoot;
	NioEventLoopGroup bossLoopGroup;
	NioEventLoopGroup workerLoopGroup;
	
	
	public Server() {
		sBoot = new ServerBootstrap();
		sBoot.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		ChannelInitializer<Channel>initializer = new ChannelInitializer<Channel>() {
			// 채팅 서버의 채널을 초기화
			@Override
			protected void initChannel(Channel ch) throws Exception {
//				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(3000,Delimiters.lineDelimiter()));
//				ch.pipeline().addLast(new StringDecoder());
//				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new ServerHandler(pool, shutdownServer));
			}
		};
		bossLoopGroup = new NioEventLoopGroup(1);
		workerLoopGroup = new NioEventLoopGroup();
		serverOff();
		sBoot.group(bossLoopGroup, workerLoopGroup).channel(NioServerSocketChannel.class).childHandler(initializer);
	}
	
	public void run() {
		ChannelFuture future = sBoot.bind(new InetSocketAddress(PORT));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					LOGGER.info("서버를 시작합니다.");
				}else {
					LOGGER.error("서버 시작 중 에러가 발생했습니다. 다시 한번 시도해 주시기 바랍니다.", future.cause());
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
		shutdownServer = new Runnable () {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					LOGGER.info("서버를 종료합니다.");
					bossLoopGroup.shutdownGracefully().sync();
					pool.shutdown();
					pool.awaitTermination(5, TimeUnit.SECONDS);
				}catch(InterruptedException e) {
					LOGGER.error("서버를 종료하는 도중 에러가 발생했습니다.", e);
				}		
			}
		};
	}
	
	public static void main (String[] args) {
		Server server = new Server();
		server.run();
	}
}
