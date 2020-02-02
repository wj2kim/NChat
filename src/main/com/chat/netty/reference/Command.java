package com.chat.netty.reference;

import java.io.Serializable;

public abstract class Command implements Serializable{
	
	private CommandType commandType;
	private String content;
	
	public Command(CommandType commandType) {
		this.commandType = commandType;
	}
	
	public Command() {
		// TODO Auto-generated constructor stub
	}

	public CommandType getCommandType() {
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
