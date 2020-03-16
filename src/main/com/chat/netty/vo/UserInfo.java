package com.chat.netty.vo;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;

public class UserInfo {
	
	private String userName;
	private ChannelHandlerContext ctx;
	
	private Date userCreationDate;
	
	
	public UserInfo(String userName) {
		this.userName = userName;
	}

	public UserInfo(ChannelHandlerContext ctx) {
		Date today = new Date();
		userCreationDate = today;
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

	public Date getUserCreationDate() {
		return userCreationDate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	
	
	
	
	
	
	
	

}
