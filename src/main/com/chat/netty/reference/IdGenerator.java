package com.chat.netty.reference;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator implements IdGeneratorInterface {
	
	private AtomicInteger currentId = new AtomicInteger(100);
	private static final IdGenerator defaultId = new IdGenerator();
	
	public IdGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public static IdGenerator getDefautId() {
		return defaultId;
	}
	
	@Override
	public int nextId() {
		return currentId.getAndIncrement();
	}

}
