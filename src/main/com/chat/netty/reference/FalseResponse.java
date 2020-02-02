package com.chat.netty.reference;

public class FalseResponse extends Message{

//	private final String commandType;
	private final String content;
	
	public FalseResponse(String commandType, String content) {
		super(MessageType.forType(commandType));
//		this.commandType = commandType;
		this.content = content;
	}

	
}
