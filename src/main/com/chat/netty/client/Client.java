package com.chat.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.chat.netty.vo.ChannelInfo;
import com.chat.netty.vo.UserInfo;

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
	private final ExecutorService pool = Executors.newCachedThreadPool();
	
	UserInfo userInfo = new UserInfo();
	ChannelInfo channelInfo = new ChannelInfo();

	
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
					System.out.println();
					System.out.print(" --------------------------------------------- \r\n");
					System.out.print(" ----------- NChat에 오신것을 환영합니다 ----------- \r\n");
					System.out.print(" --------------------------------------------- \r\n");
					System.out.println();
					mainGate(future.channel().id().toString());
				}else {
					LOGGER.error("서버와의 연결에 실패했습니다. 다시 한번 시도해 주시기 바랍니다.",future.cause());
				}
			}
		});
		
//		
//		ChannelFuture lastWriteFuture = null;
//	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        for (;;) {
//	        	try {
//	        		String line = in.readLine();
//	        		if (line == null) {
//	        			break;
//	        		}
//	        		
//	        		// Sends the received line to the server.
//	        		lastWriteFuture = future.channel().writeAndFlush(line + "\r\n");
//	        		
//	        		// If user typed the 'bye' command, wait until the server closes
//	        		// the connection.
//	        		if ("bye".equals(line.toLowerCase())) {
//	        			future.channel().closeFuture().sync();
//	        			break;
//	        			
//	        			// Wait until all messages are flushed before closing the channel.
//	        		}
//	        	}catch(InterruptedException e) {
//	        		e.printStackTrace();
//	        	} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}
//		if (lastWriteFuture != null) {
//			try {
//				lastWriteFuture.sync();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
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
	
	private void mainGate(String userId) {
		// NChat의 시작 루프. 닉네임 먼저 설정하기 -> 대기실
		pool.execute(new Runnable() {
			@Override
			public void run() {

				boolean nameSetNeeded = true;
				do {
					try {
						System.out.print("닉네임을 입력하세요 (2자~10자) : \r\n");
						String line = setUserName();
						if (line!=null) {
							userInfo.setUserName(line); 
							userInfo.setUserId(userId);
							// 대기실로 연결
							channelInfo.setUserInfo(userInfo);
							waitingRoom();
//							nameSetNeeded = false;
						}else {
							System.out.print("닉네임을 형식에 맞게 입력해 주시기 바랍니다. \r\n");
							// 제대로 된 형식의 닉네임을 입력할때 까지 무한 반복
							continue;
						}					
					}catch(Exception e) {
						e.printStackTrace();
						break;
					}
				} while (nameSetNeeded);	
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
    
    
    private void waitingRoom() {
    	boolean resetUserName = false;
    	do {
    		System.out.println("waitingRoom 입장.");
    		System.out.println(channelInfo.getCtx());
    		System.out.println(channelInfo.getUserInfo().getUserId());
    		System.out.println(channelInfo.getUserInfo().getUserName());
    	}while(resetUserName);
    }
	
	

}
