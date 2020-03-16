package com.chat.netty.reference;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelId;

public class PacketIntegration {
	
	private Map<ChannelId, byte [] >packetMap = new HashMap<ChannelId, byte [] >();
	private Map<ChannelId, Integer >packetSizeMap = new HashMap<ChannelId, Integer >();
	PacketFactory factory;
	byte [] integratedPacket;
	
	
	public PacketIntegration() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void setMotherPacket(ChannelId id, byte [] packet, int packetSize) {
		packetMap.put(id, packet); // Channeled 와 packet 저장 맵 
		packetSizeMap.put(id, packetSize); // Channeled 와 받아야 하는 size 저장 맵 
	}
	
	
	public boolean isExist(ChannelId id) {
		if(packetMap.containsKey(id)) {
			return true;
		}
		return false;
	}
	
	
	public boolean isIntegrated(ChannelId id) { // 패킷이 완전한 상태로 합쳐 졌는지 확인 
		if(packetMap.get(id).length == packetSizeMap.get(id)) {
			return true;
		}
		return false;
	}
	
	
	public void add(ChannelId id, byte [] packet) {
		if(!isIntegrated(id)) { // 통합이 안됬을 시
			byte [] motherPacket = packetMap.get(id); // mother 패킷과 새로 들어오는 패킷을 붙여준다. 	
			if(motherPacket != null ) {
				int packetMapLength = motherPacket.length;
				int packetLength = packet.length;
				byte [] combinedPacket = new byte [packetMapLength + packetLength]; // 기존 패킷 + 새로들어온 바이트 배열을 합친다.
				System.arraycopy(motherPacket, 0, combinedPacket, 0,packetMapLength);
				System.arraycopy(packet, 0, combinedPacket, packetMapLength, packetLength);
				packetMap.put(id, combinedPacket);// 합친 배열을 다시 맵에 저장한다. 키가 중복값이면 마지막으로 넣은 값으로 대체한다.
			}
		}
	}
	
	
	public byte [] getIntegratedPacket(ChannelId id) {
		integratedPacket = packetMap.get(id); // 통합된 패킷을 가져온다 
		packetMap.remove(id); // 통합된 패킷을 가지고 오면 맵에 있는 패킷을 지운다.
		packetSizeMap.remove(id); //사이즈 맵에 있는 정보도 같이 지운다.
		return integratedPacket;
	}
	
	
	public void remove(ChannelId id) { // 유저가 보낸 패킷의 통합과정을 거치는 도중 유저가 끊겼을때 이용.
		if(packetMap.containsKey(id)) {
			packetMap.remove(id); // 통합된 패킷을 가지고 오면 맵에 있는 패킷을 지운다.
			packetSizeMap.remove(id); //사이즈 맵에 있는 정보도 같이 지운다.			
		}
	}
	
	
	
	
	
	
	
	
	
}
