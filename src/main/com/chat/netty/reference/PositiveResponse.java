package com.chat.netty.reference;

import java.util.Map;

import com.chat.netty.vo.UserInfo;

public class PositiveResponse extends Message{
	
	private final String content;
	private final Map<String, String> strMap;
	
	public PositiveResponse(String commandType, String content, Map<String,String> strMap) {
		super(MessageType.forType(commandType));
		this.content = content;
		this.strMap = strMap;
	}

	public Map<String, String> getStrMap() {
		return strMap;
	}


	
	
	
	
	
	
	

}
