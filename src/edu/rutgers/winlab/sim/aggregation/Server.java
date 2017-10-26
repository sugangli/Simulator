package edu.rutgers.winlab.sim.aggregation;

import edu.rutgers.winlab.sim.aggregation.Aggregator.AggregatedData;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;


public class Server extends Node {
	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			Payload data = (Payload)parameter.getMacpayload();
			System.out.printf("Node=%s Now=%f Packet=%s%n", Server.this, EventQueue.Now(), parameter);
			if (data == null) return 0;
			return 0;
		}
	};

	public Server(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		setProcessPacket(processPacket);
	}

}
