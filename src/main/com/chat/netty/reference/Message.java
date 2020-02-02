package com.chat.netty.reference;

import java.io.Serializable;

public abstract class Message implements Serializable{
	
	private MessageType commandType;
	private String content;
	
	public Message(MessageType commandType) {
		this.commandType = commandType;
	}
	
	public Message() {
		// TODO Auto-generated constructor stub
	}

	public MessageType getCommandType() {
		return commandType;
	}

	public String getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		
		return super.toString();
	}

}
