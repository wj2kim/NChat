package com.chat.netty.reference;

public class DisposablePacketStorage {
	
	private static byte [] tempPacket = null; // 임시 패킷 저장소
	private static int count = 0;
	private static int maxCount = 3; // maximum count down = 세번
	
	public DisposablePacketStorage() {
	}
	
	public static void setToStorage(byte [] packet ) {
		tempPacket = packet;
	}
	
	public static byte [] getFromStorage() {
		return tempPacket;
	}
	
	public static void removeFromStorage() {
		IdleStateCountRefresh();
		tempPacket = null;
	}
	
	public static void IdleStateCountRefresh() {
		count = 1; // 1 으로 초기화
	}
	
	public static void IdleStateCount() {
		count += 1; 
	}
	
	public static boolean isMaxIdleStateCount() {
		if ( count > maxCount ) { 
			return true;
		}
		return false;
	}

}
