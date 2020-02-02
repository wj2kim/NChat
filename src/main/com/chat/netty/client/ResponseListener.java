package com.chat.netty.client;

import com.chat.netty.reference.Message;

public interface ResponseListener {
	
	// 서버에서 오는 응답을 받는 인터페이스
	
	void onResponse(Message response);
	
	long getTimeoutMillis();
	
	void onTimeout(Message response);

}
