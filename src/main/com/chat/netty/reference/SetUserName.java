package com.chat.netty.reference;

public class SetUserName extends Message{

	private final String content;
	
	public SetUserName(String content) {
		super(MessageType.SET_USERNAME);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	
	
	
}
