package edu.rutgers.winlab.sim.aggregation;

import java.util.HashMap;

import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Serial;

public class Aggregator extends Node {
	
	

	private HashMap<String, Double> aggregateMap = new HashMap<String, Double>(); // <Aggregated Object ID, Time Window>
	public Aggregator(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	private Serial.SerialAction<MACPacket> aggregatePacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			// TODO Auto-generated method stub
			return 0;
		}};

}
