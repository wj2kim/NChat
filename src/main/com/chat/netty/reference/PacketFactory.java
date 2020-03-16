package com.chat.netty.reference;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketFactory {

	private final String convertTo = "UTF-8";
	
	private static final int bufferSize = 1024; 
	private int offset = 0; 
	private ByteBuffer buffer;
	
	public PacketFactory() {
		this(bufferSize);
	}
	
	public PacketFactory(int size) {
		buffer = ByteBuffer.allocate(size);
		buffer.clear();
	}
	
	
	public PacketFactory(byte[]data) {
		this(data.length);
		buffer = ByteBuffer.wrap(data);
	}
	
	
	public byte[] finish() {
		
		byte [] data = {};
		offset = buffer.position(); // 마지막 포인터의 위치 기억 
		
		if(buffer.hasArray()) { // 버퍼에 array 가 존재한다면 
			data = buffer.array();
		}

		byte [] result = new byte[offset];
		System.arraycopy(data, 0, result, 0, offset); // offset 크기이면서 값이 들어간 배열 생성
		
		buffer.flip();
		return result;
	}
	
	
	
	
	public void setMessageType(byte protocol) {
		buffer.put(protocol);
	}
	
	
	public void setToBigEndian() {
		if(buffer.order() != ByteOrder.BIG_ENDIAN) {
			buffer.order(ByteOrder.BIG_ENDIAN);
		}
	}
	
	
	public void setToLittleEndian() {
		if(buffer.order() != ByteOrder.LITTLE_ENDIAN) {
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
	}
	
	public void set(short param) {
		if(buffer.remaining() > Short.SIZE / Byte.SIZE) { 
			// 남은 공간이 있을 경우 
			buffer.putShort(param);			
		}
	}
	
	
	public void set(int param) {
		if(buffer.remaining() > Integer.SIZE / Byte.SIZE) { 
			// 남은 공간이 있을 경우 
			buffer.putInt(param);			
		}
	}
	
	public void set(long param) {
		if(buffer.remaining() > Long.SIZE / Byte.SIZE) { 
			// 남은 공간이 있을 경우 
			buffer.putLong(param);			
		}
	}
	
	
	public void set(String param) {
		int length = param.getBytes().length;
		if(buffer.remaining() > length ) { // 남은 공간이 있을 경우
			buffer.putShort((short)length);
			try {
				buffer.put(param.getBytes(convertTo));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ByteBuffer getBuffer() {
		return buffer;
	}
	
	public short getSize() {
		return buffer.getShort();
	}
	
	
	public byte getMessageType() {
		return buffer.get();
	}
	
	
	public int getId() {
		return buffer.getInt();
	}
	
	
	public String getString() {
		short length = buffer.getShort();
		byte [] temp = new byte[length];
		
		buffer.get(temp);
		String result = null;
		try {
			result = new String(temp, convertTo);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	
	
	
}
