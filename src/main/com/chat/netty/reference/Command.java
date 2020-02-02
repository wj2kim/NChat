package com.chat.netty.reference;

public abstract class Command {
	
	private CommandType commandType;
	private String content;
	
	public Command(CommandType commandType) {
		this.commandType = commandType;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public String getContent() {
		return content;
	}
	
	

}
