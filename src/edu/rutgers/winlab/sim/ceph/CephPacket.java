package edu.rutgers.winlab.sim.ceph;

import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Payload;

public class CephPacket extends Payload{
	
	private long SizeInBits;
	
	public void setSizeInBits(long sizeInBits) {
		SizeInBits = sizeInBits;
	}

	public CephPacket(String val, long sizeInBits) {
		super(val);
		SizeInBits = sizeInBits;
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return String.format("CephPacket:{%s}", getVal());
	}

	@Override
	public long getSizeInBits() {
		
		return SizeInBits;
	}

}
