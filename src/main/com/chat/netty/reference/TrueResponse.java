package com.chat.netty.reference;

public class TrueResponse extends Command{
	
	private final String content;
	
	public TrueResponse(String commandType, String content) {
		super(CommandType.forType(commandType));
		this.content = content;
	}

}
