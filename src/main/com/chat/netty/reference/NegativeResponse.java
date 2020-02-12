package com.chat.netty.reference;

public class NegativeResponse extends Message{

//	private final String commandType;
	private final String content;
	
	public NegativeResponse(String commandType, String content) {
		super(MessageType.forType(commandType));
//		this.commandType = commandType;
		this.content = content;
	}

	
}
