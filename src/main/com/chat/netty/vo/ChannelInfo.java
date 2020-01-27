package com.chat.netty.vo;



import io.netty.channel.ChannelHandlerContext;

public class ChannelInfo {
    private ChannelHandlerContext ctx; 
    private UserInfo userInfo;
    
    
	public ChannelInfo(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
	}
	
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
    
    

}
