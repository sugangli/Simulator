package edu.rutgers.winlab.sim.aggregation;

import edu.rutgers.winlab.sim.core.Payload;

public class RoutingPayload extends Payload{

	public RoutingPayload(String val) {
		super(val);
		// TODO Auto-generated constructor stub
	}
	
	public String getSrc() {
		String[] sc = getVal().split(",");
		return sc[0];
	}
	
	public String getDst() {
		String[] sc = getVal().split(",");
		return sc[1];
	}
}
