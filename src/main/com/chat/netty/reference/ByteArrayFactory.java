package com.chat.netty.reference;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.chat.netty.vo.RoomInfo;

public class ByteArrayFactory {
	
	
	public static byte [] byteArrayFactory(MessageType type) {
	   byte[] packet = null;
	   if(type != null ) {
		   PacketFactory factory = new PacketFactory(5); // id 값의 4byte + messageType 의 1byte = 총 5 byte 로 고정 설정함.
		   
		   factory.set(uniqueIdGenerator()); 
		   factory.setMessageType(type.getByte()); // 해당 타입의 byte 를 가져와 세팅하기 ( only )
		   byte[] initData = factory.finish(); // 받은 정보들을 가지고 byte [] 만들기 
		   
		   // 배열의 가장 앞쪽에 initData.length를 통해 받아와야하는 길이를 넣어야 함으로 새로운 바이트 배열을 만들어서 합치는 방법을 사용함
		   ByteBuffer buffer = ByteBuffer.allocate(2); // initPacket을 재 사용할 수 있지만 2바이트만을 사용하기 때문에 로 2바이트 버퍼를 새로 할당하는 방법을 선택함
		   buffer.putShort((short)(initData.length +2)); // 자신의 길이 (2 byte) 값도 추가해서 만듬. short 를 사용한 이유는 데이터의 총 길이가 short의 음수 제외 최대 값인  32767 값을 넘을 일이 없기 때문.
		   byte[] dataLength = buffer.array(); 
		   
		   packet = Arrays.copyOf(dataLength, dataLength.length + initData.length);
		   System.arraycopy(initData, 0, packet, dataLength.length, initData.length); // dataLength 와 initData 를 합쳐준다, 단 dataLength가 앞으로 온다.
		   
		   // test
//		   System.out.println("packet.length : " + packet.length);
//		   for(int i = 0 ; i< packet.length; i++ ) {
//			   System.out.println("data : " + packet[i]);
//		   }
		   
		   return packet;  
	   }
	   return packet;
	}
	
	
	public static byte [] byteArrayFactory(MessageType type, String singleContent) {
	   byte[] packet = null;
	   if(type != null && singleContent != null) {
		   PacketFactory factory = new PacketFactory();
		   		   
		   factory.set(uniqueIdGenerator()); 
		   factory.setMessageType(type.getByte()); // 해당 타입의 byte 를 가져와 세팅하기
		   factory.set(singleContent); // 바로 이어서 유저 한테 받은 String 값 세팅하기 
		   byte[] initData = factory.finish(); // 받은 정보들을 가지고 byte [] 만들기 
		   
		   // 배열의 가장 앞쪽에 initData.length를 통해 받아와야하는 길이를 넣어야 함으로 새로운 바이트 배열을 만들어서 합치는 방법을 사용함
		   ByteBuffer buffer = ByteBuffer.allocate(2); // initPacket을 재 사용할 수 있지만 2바이트만을 사용하기 때문에 로 2바이트 버퍼를 새로 할당하는 방법을 선택함
		   buffer.putShort((short)(initData.length +2)); // 자신의 길이 (2 byte) 값도 추가해서 만듬. short 를 사용한 이유는 데이터의 총 길이가 short의 최대 값인 32767 값을 넘을 일이 없기 때문.
		   byte[] dataLength = buffer.array(); 
		   
		   packet = Arrays.copyOf(dataLength, dataLength.length + initData.length);
		   System.arraycopy(initData, 0, packet, dataLength.length, initData.length); // dataLength 와 initData 를 합쳐준다, 단 dataLength가 앞으로 온다.
		   
		   // test
//		   System.out.println("packet.length : " + packet.length);
//		   for(int i = 0 ; i< packet.length; i++ ) {
//			   System.out.println("data : " + packet[i]);
//		   }
		   			   
		   return packet;  
	   }
	   return packet;
	}
	
	
	public static byte [] byteArrayFactory(MessageType type, String contentA, String contentB) {
	   byte[] packet = null;
	   if((type != null && contentA != null && !contentA.equals(""))
			  && ( type !=null && contentB != null )) { // contentB 에는 password 공백값이 올 수 있다.  
		   PacketFactory factory = new PacketFactory();
		   		   
		   factory.set(uniqueIdGenerator());  
		   factory.setMessageType(type.getByte()); // 해당 타입의 byte 를 가져와 세팅하기
		   factory.set(contentA); // 바로 이어서 유저 한테 받은 String 값 세팅하기 
		   factory.set(contentB);
		   byte[] initData = factory.finish(); // 받은 정보들을 가지고 byte [] 만들기 
		   
		   // 배열의 가장 앞쪽에 initData.length를 통해 받아와야하는 길이를 넣어야 함으로 새로운 바이트 배열을 만들어서 합치는 방법을 사용함
		   ByteBuffer buffer = ByteBuffer.allocate(2); // initPacket을 재 사용할 수 있지만 2바이트만을 사용하기 때문에 로 2바이트 버퍼를 새로 할당하는 방법을 선택함
		   buffer.putShort((short)(initData.length +2)); // 자신의 길이 (2 byte) 값도 추가해서 만듬. short 를 사용한 이유는 데이터의 총 길이가 short의 최대 값인 32767 값을 넘을 일이 없기 때문.
		   byte[] dataLength = buffer.array(); 
		   
		   packet = Arrays.copyOf(dataLength, dataLength.length + initData.length);
		   System.arraycopy(initData, 0, packet, dataLength.length, initData.length); // dataLength 와 initData 를 합쳐준다, 단 dataLength가 앞으로 온다.
		   
		   // test
//		   System.out.println("packet.length : " + packet.length);
//		   for(int i = 0 ; i< packet.length; i++ ) {
//			   System.out.println("data : " + packet[i]);
//		   }
		   			   
		   return packet;  
	   }
	   return packet;
	}
	
	public static byte [] byteArrayFactory(MessageType type, String contentA, String contentB, String contentC) {
	   byte[] packet = null;
	   if((type != null && contentA != null && !contentA.equals(""))
			  && ( type !=null && contentB != null && !contentB.equals(""))
			  && (type !=null && contentC != null && !contentC.equals(""))) {
		   PacketFactory factory = new PacketFactory();
		   		   
		   factory.set(uniqueIdGenerator()); 
		   factory.setMessageType(type.getByte()); // 해당 타입의 byte 를 가져와 세팅하기
		   factory.set(contentA); // 바로 이어서 유저 한테 받은 String 값 세팅하기 
		   factory.set(contentB);
		   factory.set(contentC);
		   byte[] initData = factory.finish(); // 받은 정보들을 가지고 byte [] 만들기 
		   
		   // 배열의 가장 앞쪽에 initData.length를 통해 받아와야하는 길이를 넣어야 함으로 새로운 바이트 배열을 만들어서 합치는 방법을 사용함
		   ByteBuffer buffer = ByteBuffer.allocate(2); // initPacket을 재 사용할 수 있지만 2바이트만을 사용하기 때문에 로 2바이트 버퍼를 새로 할당하는 방법을 선택함
		   buffer.putShort((short)(initData.length +2)); // 자신의 길이 (2 byte) 값도 추가해서 만듬. short 를 사용한 이유는 데이터의 총 길이가 short의 최대 값인 32767 값을 넘을 일이 없기 때문.
		   byte[] dataLength = buffer.array(); 
		   
		   packet = Arrays.copyOf(dataLength, dataLength.length + initData.length);
		   System.arraycopy(initData, 0, packet, dataLength.length, initData.length); // dataLength 와 initData 를 합쳐준다, 단 dataLength가 앞으로 온다.
		   
		   // test
//		   System.out.println("packet.length : " + packet.length);
//		   for(int i = 0 ; i< packet.length; i++ ) {
//			   System.out.println("data : " + packet[i]);
//		   }
		   			   
		   return packet;  
	   }
	   return packet;
	}
	
	
	// roomList 
	
	@SuppressWarnings({ "unchecked", "rawtypes" }) // 미확인 오퍼레이션과 관련된 경고를 억제, 원시 유형 사용법과 관련된 경고를 억제
	public static byte [] byteArrayFactory(MessageType type, Map data, String infoType) {
		byte[] packet = null;
		
		if( type != null && data != null && !data.isEmpty()) {
			PacketFactory factory = new PacketFactory();
						
			factory.set(uniqueIdGenerator()); 
			factory.setMessageType(type.getByte()); // 해당 타입의 byte 를 가져와 세팅하기
			
			if(infoType.equals("roomInfo")) {	// 해당 맵의 제네릭이 <String, roomInfo> 일때
				RoomInfo roomInfo = null;
				Iterator<String> iter = data.keySet().iterator();				
				factory.set((short)data.size()); // roomInfo 를 담은 맵 객체의 사이즈 float 값으로 세팅하기
				while(iter.hasNext()) {
					String key = iter.next();
					roomInfo = (RoomInfo) data.get(key); // roomInfo 하나씩 가지고 와서 내부 정보를 패킷에 하나씩 붙이기.
					factory.set(roomInfo.getRoomName());
					factory.set(roomInfo.getRoomPassword());
					factory.set(roomInfo.getRoomMaster());
					factory.set(roomInfo.passwordPresence());
					factory.set(roomInfo.getUsersInRoom().size()+ "/" + roomInfo.getMaxNoOfUsers());
				}							
			}
			
			if(infoType.equals("userInfo")) {	// 해당 맵의 제네릭이 <String, UserInfo> 일때
				Iterator<String> iter = data.keySet().iterator();				
				factory.set((short)data.size()); // userInfo 를 담은 맵 객체의 사이즈 float 값으로 세팅하기
				while(iter.hasNext()) {
					String key = iter.next();
					factory.set(key);	
				}
			}
		   byte[] initData = factory.finish(); // 받은 정보들을 가지고 byte [] 만들기 
		   // 배열의 가장 앞쪽에 initData.length를 통해 받아와야하는 길이를 넣어야 함으로 새로운 바이트 배열을 만들어서 합치는 방법을 사용함
		   ByteBuffer buffer = ByteBuffer.allocate(2); // initPacket을 재 사용할 수 있지만 2바이트만을 사용하기 때문에 로 2바이트 버퍼를 새로 할당하는 방법을 선택함
		   buffer.putShort((short)(initData.length +2)); // 자신의 길이 (2 byte) 값도 추가해서 만듬. short 를 사용한 이유는 데이터의 총 길이가 short의 음수 제외 최대 값인 32767 값을 넘을 일이 없기 때문.
		   byte[] dataLength = buffer.array(); 
		   
		   packet = Arrays.copyOf(dataLength, dataLength.length + initData.length);
		   System.arraycopy(initData, 0, packet, dataLength.length, initData.length); // dataLength 와 initData 를 합쳐준다, 단 dataLength가 앞으로 온다.
		   
		   // test
//		   System.out.println("packet.length : " + packet.length);
//		   for(int i = 0 ; i< packet.length; i++ ) {
//			   System.out.println("data : " + packet[i]);
//		   }
		   			   
		   return packet;  	
		}
		return packet;
	}	
	
	private static int uniqueIdGenerator() { // 패킷의 unique id 생성 메소드 
		int randomNumId = (int)(Math.random()*9999999);
		int randomDateId = (int) (System.currentTimeMillis() & 0xfffffff); 
		int uniqueId = randomDateId - randomNumId ;
		return uniqueId; 
	}

}
