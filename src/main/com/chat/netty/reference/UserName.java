package com.chat.netty.reference;

public class UserName {
	
	private final int maxLength = 10;
	private final String userName;
	
	public UserName(String userName) {
		if(userName.length()>maxLength) {
			throw new IllegalArgumentException("닉네임이 너무 깁니다. 다시 설정해주시기 바랍니다.(1~10자)");
		}
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
//	Test
//	public static void main(String[] args) {
//		UserName u = new UserName("쿠프입니다안녕하세요요");
//		System.out.println(u.getUserName());
//	}
	
	

}
