package com.chat.netty.client;

import com.chat.netty.reference.Command;

public interface ResponseListener {
	
	// 서버에서 오는 응답을 받는 인터페이스
	
	void onResponse(Command response);
	
	long getTimeoutMillis();
	
	void onTimeout(Command response);

}
