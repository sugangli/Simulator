package edu.rutgers.winlab.sim.ceph;

import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Payload;

public class CephPacket extends Payload{
	
	private int SizeInBits;
	
	public void setSizeInBits(int sizeInBits) {
		SizeInBits = sizeInBits;
	}

	public CephPacket(String val, int sizeInBits) {
		super(val);
		SizeInBits = sizeInBits;
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return String.format("CephPacket:{%s}", getVal());
	}

	@Override
	public int getSizeInBits() {
		
		return SizeInBits;
	}

}
