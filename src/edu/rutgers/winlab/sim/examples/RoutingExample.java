package edu.rutgers.winlab.sim.examples;

import java.io.IOException;
import java.util.HashMap;

import edu.rutgers.winlab.sim.aggregation.AccessPoint;
import edu.rutgers.winlab.sim.aggregation.Dijkstra;
import edu.rutgers.winlab.sim.aggregation.Router;
import edu.rutgers.winlab.sim.aggregation.RoutingPayload;
import edu.rutgers.winlab.sim.aggregation.Server;
import edu.rutgers.winlab.sim.aggregation.Dijkstra.DijkstraResult;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.TopoManager;

public class RoutingExample {
	
	public static void main(String[] args) {
		String filename = "topo.txt";
		HashMap<String, Router> map = new HashMap<>();
		try {
			TopoManager.initRouters(map, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AccessPoint ap1 = new AccessPoint("ap1");
		AccessPoint ap2 = new AccessPoint("ap2");
		
		Node.AddNodeLink(ap1, map.get("r1"), 10);
		Node.AddNodeLink(ap2, map.get("r4"), 10);
		
		Server s1 = new Server("s1");
		Node.AddNodeLink(s1, map.get("r3"), 10);
		
		for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(s1)){
//        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
        	Router r;
        	if(dij.From.getName().startsWith("ap") || dij.From.getName().startsWith("s")) {
        		continue;
        	}else {
        		r = (Router) dij.From;
        		r.insertRoutingTable(s1.getName(), dij.NextHop);
        	}
        	
        }
		
		ap1.sendPacket(new MACPacket(ap1, map.get("r1"), new RoutingPayload("ap1,s1,100")), false);
		ap2.sendPacket(new MACPacket(ap2, map.get("r4"), new RoutingPayload("ap2,s1,200")), false);
		EventQueue.Run();
		return;
	}

}
