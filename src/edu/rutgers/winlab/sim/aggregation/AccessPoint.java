package edu.rutgers.winlab.sim.aggregation;

import java.util.Map;

import org.apache.commons.math3.distribution.PoissonDistribution;

import edu.rutgers.winlab.sim.aggregation.ComputeNode.AggregatedData;
import edu.rutgers.winlab.sim.core.Action;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;

public class AccessPoint extends Node {
	double dataRate = 0;
	Node first_anode;

	public Node getFirstANode() {
		return first_anode;
	}

	public void setFirstANnode(Node first_anode) {
		this.first_anode = first_anode;
	}

	public AccessPoint(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public double getDataRate() {
		return dataRate;
	}

	public void setDataRate(double dataRate) {
		this.dataRate = dataRate;
	}
	
	public void generateTrafficByID(int object_id) {
		PoissonDistribution pd = new PoissonDistribution(this.dataRate);
		
		EventQueue.AddEvent(EventQueue.Now(), send_after_interval, pd, object_id);
		
	}
	
	private Action send_after_interval = new Action() {

		@Override
		public void execute(Object... args) {
			PoissonDistribution pd  = (PoissonDistribution)args[0];
			double occurs  = pd.sample();
			if(occurs != 0) {
				int object_id = (int) args[1];
				for(Map.Entry<Node, Node.LinkState> entry: AccessPoint.this.getNeighbors().entrySet()) {
					MACPacket new_packet = new MACPacket(AccessPoint.this, entry.getKey(), new AggregatedData(AccessPoint.this.getName() + "," 
								+ getFirstANode().getName() + "," + object_id + "," + EventQueue.Now()));;
					AccessPoint.this.sendPacket(new_packet, false);
//					System.out.printf("Now=%f Node=%s Packet=%s, occurs=%f%n", EventQueue.Now(), AccessPoint.this, new_packet, occurs);
					EventQueue.AddEvent(EventQueue.Now() + 1/occurs, send_after_interval, pd, object_id);
				}
			}	
		}
		
	};
	
	

}
