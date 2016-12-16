package edu.rutgers.winlab.sim.core;

public class MACPacket implements ISerializable {

	public final static int MAC_PACKET_HEADER_SIZE = 9 * ISerializableHelper.BYTE;
	public Node From;
	public Node To;
	public Payload Macpayload;

	public MACPacket(Node from, Node to, Payload macpayload){

		From = from;
		To = to;
		Macpayload = macpayload;
	}

	public Payload getMacpayload() {
		return Macpayload;
	}

	public void setMacpayload(Payload macpayload) {
		Macpayload = macpayload;
	}

	@Override
	public int getSizeInBits() {
		
		return Macpayload.getSizeInBits() + MAC_PACKET_HEADER_SIZE * 8;
	}

	
	public String toString(){
		return String.format("MAC:{From:%s, To:%s, Payload:%s}", From, To, Macpayload);
	}

}
