package com.chat.netty.client;

import java.net.InetSocketAddress;
import java.util.Scanner;

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

public class NChatClient {
	
	private static final Logger LOGGER = Logger.getLogger(NChatClient.class.getName());
	private static final int PORT = 8080;
	private Bootstrap boot;
	private NioEventLoopGroup workerLoopGroup;
	
	public NChatClient() {
		ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new NChatClientHandler());
			}
		};
		workerLoopGroup = new NioEventLoopGroup();
		boot = new Bootstrap();
		boot.group(workerLoopGroup).channel(NioSocketChannel.class).handler(initializer);
	}
	
	public void start() {
		ChannelFuture future = boot.connect(new InetSocketAddress(PORT));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					LOGGER.info("서버와의 연결에 성공하였습니다.");
				}else {
					LOGGER.error("서버와의 연결에 실패했습니다. 다시 한번 시도해 주시기 바랍니다.",future.cause());
				}
			}
		});
		
//		ChannelFuture writtenFuture = null;
//		Scanner sc = new Scanner(System.in);
//		String written = sc.nextLine();
//		
//		writtenFuture = future.channel().writeAndFlush(written+"/r/n");
		
		
		try {
			future.channel().closeFuture().sync();
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
		NChatClient client = new NChatClient();
		client.start();
	}
	
	

}
