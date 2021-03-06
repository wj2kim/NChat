package com.chat.netty.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
//		boot.option(ChannelOption.SO_REUSEADDR, true); // 소켓 프로그램이 종료되도 커널 단에서 소켓을 바인딩 해서 사용하고 있음으로 옵션을 이용해서 기존 바인딩 주소를 다시 사용할 수 있게 함.
//		boot.option(ChannelOption.SO_KEEPALIVE, true); // 상대 TCP 에 주기적으로 ( 약 2시간 간격) 으로 TCP 패킷을 보내어 연결상태가 유지되고 있는지 확인한다.
		ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// TODO Auto-generated method stub
				ch.pipeline().addLast(new ByteArrayDecoder());
				ch.pipeline().addLast(new ByteArrayEncoder());
				ch.pipeline().addLast(new ClientHandler());
			}
		};
		workerLoopGroup = new NioEventLoopGroup(1);
		boot.group(workerLoopGroup).channel(NioSocketChannel.class).handler(initializer); // SocketChannel - TCP를 이용해 네트워크를 통해 데이터를 읽고 쓴다.
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
			System.out.println("서버와의 연결을 해제했습니다. 프로그램을 종료합니다.");
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
