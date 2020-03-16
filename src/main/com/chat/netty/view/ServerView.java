package com.chat.netty.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.chat.netty.reference.NChatManager;
import com.chat.netty.server.RequestListener;
import com.chat.netty.server.Server;

public class ServerView {
	
	private static final Logger LOGGER = Logger.getLogger(ServerView.class.getName());
	
	public static void serverLoop(ExecutorService pool) {
		pool.execute(new Runnable() {
			@Override
			public void run() {
				boolean isExit = false;
				String line ="";
				do {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					System.out.println("명령어 종류 \"/help\"");
					try {
						line = br.readLine();
					} catch (IOException e) {
						LOGGER.info("입력 도중 에러가 발생했습니다.");
					}
					if(line.equals("/help") || line.equals("/users") || line.equals("/rooms") || line.equals("/announce")|| line.equals("/shutdown")) {
						switch(line) {
						case "/help": System.out.println("공지하기\"/announce\""); System.out.println("유저 목록 \"/users\""); System.out.println("방 목록 \"/rooms\""); System.out.println("서버 종료 \"/shutdown\""); break;
						case "/announce" : RequestListener.makeAnnouncement(); break;
						case "/users": NChatManager.printAllUsers(); break;
						case "/rooms": NChatManager.printAllRooms(); break;
						case "/shutdown": isExit = true; break;
						default : break;
						}
					}
				}while(!isExit);
				Server.shutdown();
			}
		});
	}


}
