package edu.rutgers.winlab.sim.aggregation;

import java.util.HashMap;
import java.util.HashSet;

import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;

public class Router extends Node {

	private HashMap<String, HashSet<Node>> routingTable;
	public Router(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		routingTable = new HashMap<>();
		setProcessPacket(routingPacket);
	}
	
	public void insertRoutingTable(String dst, Node nextHop) {
		HashSet<Node> nextHops = routingTable.get(dst);
//		System.out.printf("Node=%s Dst=%s NextHop=%s\n", Router.this, dst, nextHop);
        if (nextHops == null) {
        	nextHops = new HashSet<>();
        	nextHops.add(nextHop);
            routingTable.put(dst, nextHops);
            
        }else {
        	nextHops.add(nextHop);
        	routingTable.put(dst, nextHops);
        }
	}
	public void removeRouting(String dst, Node nextHop) {
        HashSet<Node> nextHops = routingTable.get(dst);
        if (nextHops != null) {
            nextHops.remove(nextHop);
        }
    }
	
	private Serial.SerialAction<MACPacket> routingPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			// TODO Auto-generated method stub
			System.out.printf("Node=%s Now=%f Packet=%s%n", Router.this, EventQueue.Now(), parameter);
			RoutingPayload payload = (RoutingPayload) parameter.getMacpayload();
			String dst = payload.getDst();
			HashSet<Node> nextHops = Router.this.routingTable.get(dst);
			if(nextHops != null) {

				for(Node n:nextHops) {
					MACPacket newpacket = new MACPacket(Router.this, n, payload);
					Router.this.sendPacket(newpacket, false);
				}
			}else {
				System.out.printf("Error: No next hop found at Node=%s", Router.this);
			}
			return 0;
		}
		
	};
	

}
