package com.chat.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.chat.netty.view.ServerView;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class Server {
	private static final String HOST = "localhost";
	private static final int PORT = 8888; // 포트번호 
	
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	
	private final static ExecutorService pool = Executors.newSingleThreadExecutor(); 
	/* 스레드 관리 객체, ExecutorService는 비동기 모드에서 task를 쉽게 실행 할 수 있도록 도와주는 프레임워크이다. 
	 ExecutorService 는 thread pool 을 자동으로 부여하고 이를 다룰 API를 제공한다.
	 newSingleThreadExecutor() - 최대 한개의 스레드까지 관리해주는 메소드. 하나의 작업이 완료된 뒤에 순차적으로 다음 작업이 실행된다.
	  */
	
	private ServerBootstrap sBoot; 	/* 네트워크 애플리케이션의 동작 방식과 환경을 설정을 도와주는 클래스이다. 예를 들어 네트워크에서 수신한 데이터를 단일 스레드로 데이터베이스에 저장하는 네트워크 애플리케이션을 구현하고자 한다면 이 애플리케이션은 8088번 포트에서 데이터를 수신하고 소켓모드는 NIO를 사용한다.
								 	이와 같은 요구사항을 부트스트랩으로 설정 가능하다. NIO소켓 모드를 지원하는 서버 소켓 채널, 데이터를 변환하여 데이터베이스에 저장하는 데이터 처리 이벤트 핸들러, 
									애플리케이션이 사용할 8088번 포트 바인딩, 단일 스레드를 지원하는 이벤트 루프에 대한 설정이 필요하다. */
	private static NioEventLoopGroup bossLoopGroup; // 부모 스레드 그룹 - 클라이언트의 연결을 기다림.
	private static NioEventLoopGroup workerLoopGroup; // 자식 스레드 그룹 - 연결된 클라이언트로 부터 데이터 입출력 및 이벤트처리를 담당함.
	
	
	public Server() {
		sBoot = new ServerBootstrap(); 
		sBoot.childOption(ChannelOption.TCP_NODELAY, false); // Nagle 알고리즘 무력화 /  클라이언트에서만 사용 
		sBoot.childOption(ChannelOption.SO_RCVBUF, 10); // 커널의 수신 버퍼의 크기를 조정한다. 수신 시스템에서 TCP는 수신된 데이터를 수신 버퍼에 저장합니다. 
		ChannelInitializer<SocketChannel>initializer = new ChannelInitializer<SocketChannel>() { 
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ByteArrayDecoder()); // ByteBuf -> byteArray
				ch.pipeline().addLast(new ByteArrayEncoder()); // byteArray -> ByteBuf
				ch.pipeline().addLast(new ServerHandler());
			}
		};
		bossLoopGroup = new NioEventLoopGroup(); 
		workerLoopGroup = new NioEventLoopGroup(1);
		sBoot.group(bossLoopGroup, workerLoopGroup).channel(NioServerSocketChannel.class).childHandler(initializer); // NioServerSocketChannel - 들어오는 TCP 연결을 수신 할 수 있고 들어오는 연결마다 SocketChannel이 만들어진다.
//		sBoot.group(bossLoopGroup, workerLoopGroup).channel(OioServerSocketChannel.class).childHandler(initializer); 

	}
	
	public void run() {
		ChannelFuture future = sBoot.bind(new InetSocketAddress(HOST, PORT)); // bind
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) { // bind 성공 시
					LOGGER.info("서버를 시작합니다.");
					ServerView.serverLoop(pool); // 서버 화면 (executorService 스레드(화면 용)  ) 
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
	
	public static void shutdown() { // 서버 종료
		try {
			LOGGER.info("클라이언트와의 연결을 모두 해제 합니다.");
			// shutdown() 과는 다르게 종료 전 테스크가 들어오지 않도록 하는 'the quiet period' 를 같는다. 만약 이 시간에 task 가 들어온다면 그 task를 accept하고'the quiet period'는 다시 시작된다.
			bossLoopGroup.shutdownGracefully().sync(); 
			workerLoopGroup.shutdownGracefully().sync(); 
			pool.shutdown(); // close executor service
			pool.awaitTermination(3, TimeUnit.SECONDS);
			LOGGER.info("서버를 완전히 종료합니다.");
		}catch(InterruptedException e) {
			LOGGER.error("서버를 종료하는 도중 에러가 발생했습니다.", e);
		}		
	}
	
	public static void main (String[] args) {
		Server server = new Server();
		server.run();
	}
}
