package edu.rutgers.winlab.sim.ceph;

import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Serial;


public class Client extends Node {


	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			CephPacket payload = (CephPacket)parameter.getMacpayload();
			if(payload == null) return 0;
			String[] s_array = payload.getVal().split(",");
			switch (s_array[1]){
			case "READ":	
				System.out.printf("Client %s get %s Size %d Time %f\n", Client.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			case "WRITEACK":
				System.out.printf("Client %s get %s Size %d Time %f\n", Client.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			case "ECWRITEACK":
				System.out.printf("Client %s get %s Size %d Time %f\n", Client.this.getName(), payload.toString(), payload.getSizeInBits(), EventQueue.Now());
				break;
			default:
				break;			
			}
			return 0;
		}
	};


	public Client(String name) {
		super(name);
		setProcessPacket(processPacket);
	}

}
