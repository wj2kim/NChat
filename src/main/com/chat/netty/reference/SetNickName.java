package com.chat.netty.reference;

public class SetNickName extends Command{

	private final String content;
	
	public SetNickName(String content) {
		super(CommandType.SET_USERNAME);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	
	
	
}
