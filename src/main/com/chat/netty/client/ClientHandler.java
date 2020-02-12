package com.chat.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.chat.netty.reference.Message;
import com.chat.netty.reference.MessageType;
import com.chat.netty.reference.NegativeResponse;
import com.chat.netty.reference.PositiveResponse;
import com.chat.netty.reference.SetUserName;
import com.chat.netty.vo.UserInfo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ClientHandler extends SimpleChannelInboundHandler<Message>{
	
	private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ExecutorService pool;
	
	public ClientHandler(ExecutorService pool) {
		this.pool = pool;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//test 
//		ctx.writeAndFlush("");
		welcomeScreen(ctx);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

//		welcomeScreen(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		// TODO Auto-generated method stub
		if(msg instanceof NegativeResponse) {
			if(msg.getCommandType() == MessageType.NAME_AlREADY_EXIST ) {
				System.out.println("해당 닉네임은 이미 사용중입니다.");
				welcomeScreen(ctx);
//				progressOrNot(false);
			}
		}
		
		if(msg instanceof PositiveResponse) {
			if(msg.getCommandType() == MessageType.NAME_SET) {
				System.out.println("해당 닉네임으로 계정이 생성됬습니다. 대기실로 이동합니다.");
				
//				UserInfo userInfo = new UserInfo();
//				if(((PositiveResponse) msg).getUserInfo() instanceof UserInfo) {
//					userInfo = ((PositiveResponse) msg).getUserInfo();					
//					System.out.println("userInfo : "+userInfo.getUserName());
//					System.out.println("userInfo : "+userInfo.getCtx());					
//				}
//				ArrayList<String>list = ((PositiveResponse) msg).getList();
//				for(String al : list) {
//					System.out.println("list 도 오냐? : "+al);
//				}
				Map<String,String>strMap = ((PositiveResponse) msg).getStrMap();
				
		        Iterator<String> mapIter = strMap.keySet().iterator();
		        while(mapIter.hasNext()){
		 
		            String key = mapIter.next();
		            String value = strMap.get( key );
		 
		            System.out.println(key+" : "+value);
		 
		        }
				
//				String [] contents = ((PositiveResponse) msg).getContents();
//				for(int i = 0; i<contents.length; i++) {
//					System.out.println("String 배열이 온다!!! : "+contents[i]);					
//				}
				mainLoop(ctx);
//				progressOrNot(true);
			}
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
	public void welcomeScreen(ChannelHandlerContext ctx) {
//		pool.execute(new Runnable() {
//			@Override
//			public void run() {
//				boolean isShutdown = false;
				String content;
//				do {
//					welcome screen
					System.out.println();
					System.out.print(" ----------------------------------------------- \r\n");
					System.out.print(" ------------ NChat에 오신것을 환영합니다 ------------ \r\n");
					System.out.print(" ----------------------------------------------- \r\n");
					content = setUserName();
					Message msg = new SetUserName(content);
					sendToServer(ctx, msg);
//					if(progreeOrNot() == false) {
//						continue;
//					}
					// 서버에서 로직 처리가 완료됬다는 응답이 오면 다음 단계로 넘어가야하는데 어떻게 할까?
//					mainLoop(ctx);
//					if (msg instanceof SetNickName) {
//						System.out.println("content : "+msg.getContent());
//						System.out.println("content : "+msg.getCommandType());
//					}
//					서버로 request 한 후 
//				}while(!isShutdown);
//			}
//		});
	}
	
	private void mainLoop(ChannelHandlerContext ctx) {
		boolean isShutdown = false;
		do {
			System.out.println();
			System.out.println("---------------------------------------------------");
			System.out.println("-------------------- NChat 대기실 -------------------");
			System.out.println("---------------------------------------------------");
			System.out.println("1) 방 참여하기");
			System.out.println("2) 방 만들기");
			System.out.println("3) 전체 사용자 목록 보기");
			System.out.println("4) 대기실에 있는 사용자 목록 보기");
			System.out.println("5) 귓속말 보내기");
			System.out.println("6) 본인 정보 확인");
			System.out.println("0) 종료하기");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int option=0;
			try {
				String line = br.readLine();
				try {
					option = Integer.parseInt(line);
					if(!(option>=0 && option<7)) {
						System.out.println("주어진 옵션의 숫자만 입력해 주시기 바랍니다.");
					}
				}catch(NumberFormatException e) {
					System.out.println("주어진 옵션의 숫자만 입력해 주시기 바랍니다.");
					continue;
				}
			}catch(IOException e) {
				e.printStackTrace();
				isShutdown = true;
			}
			if(option>=0 && option<7) {
				switch(option) {
				case 1 : chatTestByRoom(ctx); break;
//				case 2 : createRoomRequest(ctx); break; 
//				case 3 : allUsersRequest(ctx); break;
//				case 4 : waitingRoomUsersRequest(ctx); break;
//				case 5 : privateMessage(ctx); break;
//				case 6 : accountInfo(ctx); break;
				case 0 : programExitRequest(ctx);
				default : continue;
				}
			}
		}while(!isShutdown);
	}
	
	
	private String setUserName() {
		boolean isSet = false;
		String content = null;
		do {
			try {
//				System.out.print("나가기 < exit 입력 > \r\n");
				System.out.print("닉네임을 입력하세요 (2자~10자) : \r\n");
				content = validateUserName();
	            if (content!=null) {
					return content;
				}else {
					System.out.print("닉네임을 형식에 맞게 입력해 주시기 바랍니다. \r\n");
//					제대로 된 형식의 닉네임을 입력할때 까지 무한 반복
					continue;
				}					
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		} while (!isSet);	
		return content;
	}
	
	
    private String validateUserName() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
        	line = br.readLine();
            if (line == null) {
//            	빈칸이면 안됨.
                return null;
            }
            if(line.split("\\s").length > 1) {
//            	띄어쓰기가 있으면 안됨.
            	return null;
            }
            if(line.length()<2 || line.length()>10) {
//            	2자 ~ 10자 사이만 가능.
            	return null;
            }
//            if(line.toLowerCase().equals("exit")) {
////            	사용 종료.
////            	서버처럼 종료 로직을 객체에 넣을까 생각해봤지만 시스템 부하 우려가 있어 보류.
//            	System.exit(-1);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return line;
    }
    
    private void sendToServer(ChannelHandlerContext ctx, Message msg) {
    	ChannelFuture future =  ctx.writeAndFlush(msg);
    	future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("서버에게 요청을 보냈습니다.");
				}else {
					System.out.println("서버에게 요청을 보내는데 실패했습니다. 다시한번 시도해 주시기 바랍니다.");
				}
			}
		});
    	try {
			future.sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private void programExitRequest(ChannelHandlerContext ctx) {
    	ChannelFuture future = ctx.channel().close();
    	future.addListener(new ChannelFutureListener() {
    		@Override
    		public void operationComplete(ChannelFuture future) throws Exception {
    			if(future.isSuccess()) {
    				System.exit(-1);
    			}else {
    				System.out.println("프로그램 종료 중 에러가 발생했습니다. 다시한번 시도하여 주시기 바랍니다.");
    			}
    		}
    	});
    }
    
//    private static boolean progressOrNot(boolean flag) {
//   
//    	return flag;
//    }
    
    private void chatTestByRoom(ChannelHandlerContext ctx) {
    	do {
    		 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	     String line = "";
    	     try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	     
    	}while(true);
    }


    
    

	
	
}
