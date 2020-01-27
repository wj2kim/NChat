package com.chat.netty.reference;

import java.util.concurrent.atomic.AtomicInteger;

public class UserId {
	private final int userId;
	private AtomicInteger atomInt = new AtomicInteger(100);

	public UserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	private int nextId() {
		return atomInt.getAndIncrement();
	}
	
	

}
