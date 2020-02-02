package com.chat.netty.vo;

import io.netty.channel.ChannelHandlerContext;

public class UserInfo {
	
	private String userName;
	private ChannelHandlerContext ctx;
	
	
	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

	public UserInfo(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	
	
	
	
	
	
	
	

}
