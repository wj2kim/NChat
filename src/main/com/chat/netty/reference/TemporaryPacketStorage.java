package com.chat.netty.reference;

import java.util.HashMap;
import java.util.Map;

public class TemporaryPacketStorage { // 임시 패킷 저장 용도
	
	private static Map <Integer, byte [] > storage = new HashMap<Integer, byte [] > ();
	
	public TemporaryPacketStorage() {
		// TODO Auto-generated constructor stub
	}
	
	public static void checkIdandStore(int id, byte [] packet){ // duplication check 
		if(storage.containsKey(id)) {
//			System.out.println("idAlreadyExist");
			return;
		}
//		System.out.println("checkedAndStored");
		storage.put(id, packet);
	}
	
	public static void checkIdandRemove(int id) { // remove 
		if(storage.containsKey(id)) {
//			System.out.println("checkedAndRemove");
			storage.remove(id);
		}
	}
	
	
	
	
	
	
	
	
	

}
