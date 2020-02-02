package com.chat.netty.reference;

public class FalseResponse extends Command{

//	private final String commandType;
	private final String content;
	
	public FalseResponse(String commandType, String content) {
		super(CommandType.forType(commandType));
//		this.commandType = commandType;
		this.content = content;
	}

	
}
