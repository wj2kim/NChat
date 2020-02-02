package com.chat.netty.reference;

public class TrueResponse extends Message{
	
	private final String content;
	
	public TrueResponse(String commandType, String content) {
		super(MessageType.forType(commandType));
		this.content = content;
	}

}
