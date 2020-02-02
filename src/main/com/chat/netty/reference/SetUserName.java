package com.chat.netty.reference;

public class SetUserName extends Command{

	private final String content;
	
	public SetUserName(String content) {
		super(CommandType.SET_USERNAME);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	
	
	
}
