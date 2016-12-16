package edu.rutgers.winlab.sim.core;

public abstract class Action {
	
	public Action(Object... args){
		
	}

	public abstract double execute(Object... args);
	
//	public void execute(Serial<MACPacket> serial, MACPacket macpacket, Node n) {
//		// TODO Auto-generated method stub
//		
//	}

	

}
