                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         package com.chat.netty.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class Client {
	private static final String HOST = "localhost";
	private static final int PORT = 8888;
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
//	private final ExecutorService pool = Executors.newSingleThreadExecutor();
	private NioEventLoopGroup workerLoopGroup;
	private Bootstrap boot;

	
	public Client() {
		boot = new Bootstrap();
		boot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5*1000);
		boot.option(ChannelOption.TCP_NODELAY, true);
		ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ByteArrayDecoder());
				ch.pipeline().addLast(new ByteArrayEncoder());
				ch.pipeline().addLast(new ClientHandler());
			}
		};
		workerLoopGroup = new NioEventLoopGroup();
		boot.group(workerLoopGroup).channel(NioSocketChannel.class).handler(initializer);
	}
	
	public void start() {
		ChannelFuture future = boot.connect(new InetSocketAddress(HOST, PORT));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("서버와의 연결에 성공했습니다.");
				}else {
					LOGGER.error("서버와의 연결에 실패했습니다. 다시 한번 시도해 주시기 바랍니다.",future.cause());
				}
			}
		});
		
		try {
			future.channel().closeFuture().sync();
			System.out.println("서버와의 연결이 끊겼습니다. 프로그램을 종료합니다.");
			shutdown();
		}catch(InterruptedException e) {
			LOGGER.error("InterruptedException msg", e);
		}
	}
	
	public void shutdown() {
		try {
			workerLoopGroup.shutdownGracefully().sync();
			System.exit(-1);
		}catch(InterruptedException e) {
			LOGGER.error("InterruptedException msg",e);
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}	

}
