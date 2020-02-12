package com.chat.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.chat.netty.codec.CommandCodec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Client {
	private static final int PORT = 8888;
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	private final ExecutorService pool = Executors.newCachedThreadPool();
	private NioEventLoopGroup workerLoopGroup;
	private Bootstrap boot;

	
	public Client() {
		ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
//				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(3000, Delimiters.lineDelimiter()));
//				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.softCachingResolver(ClassLoader.getSystemClassLoader())));
//				ch.pipeline().addLast(new CommandCodec());
				ch.pipeline().addLast(new ClientHandler(pool));
			}
		};
		boot = new Bootstrap();
		workerLoopGroup = new NioEventLoopGroup();
		boot.group(workerLoopGroup).channel(NioSocketChannel.class).handler(initializer);
	}
	
	public void start() {
		ChannelFuture future = boot.connect(new InetSocketAddress("127.0.0.1", PORT));
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
			pool.shutdown();
			pool.awaitTermination(3, TimeUnit.SECONDS); 
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
