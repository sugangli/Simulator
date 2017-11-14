package edu.rutgers.winlab.sim.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Node.LinkState;

public class Dijkstra {
	
	public static class DijkstraResult implements Comparable<DijkstraResult> {
		public Node From;
		public Node NextHop;
		public double Weight; 
		public DijkstraResult(Node from, Node nexthop, double weight){
			this.From = from;
			this.NextHop = nexthop;
			this.Weight = weight;
		}
		@Override
		public int compareTo(DijkstraResult other) {
			if(Weight == other.Weight)
				return 0;
			else if (Weight > other.Weight)
				return 1;
			else 
				return -1;
		}
		
	}
	
	public static ArrayList<DijkstraResult> CalculateShortestPaths(Node source){
		ArrayList<DijkstraResult> ret = new ArrayList<>();
		HashSet<Node> settledNodes = new HashSet<>();
		HashMap<Node, DijkstraResult> workingTable = new HashMap<>();
		workingTable.put(source, new DijkstraResult(source, source, 0));
		settledNodes.add(source);
		
		while(workingTable.size() > 0){
			List<DijkstraResult> dij_list = new ArrayList<>( workingTable.values());
			Collections.sort(dij_list);
			DijkstraResult current = dij_list.get(0);
			ret.add(current); 
			for(Map.Entry<Node, LinkState> n: current.From.getNeighbors().entrySet()){
				if (workingTable.containsKey(n.getKey())){ 
					DijkstraResult r = workingTable.get(n.getKey());
					double newWeight = current.Weight + n.getValue().getDelay();
					if(r.Weight > newWeight){
						r.NextHop = current.From;
						r.Weight = newWeight;
					}
				}
				else if (!settledNodes.contains(n.getKey())){
					workingTable.put(n.getKey(), new DijkstraResult(n.getKey(), current.From, current.Weight + n.getValue().getDelay()));
					settledNodes.add(n.getKey());
				}
			}
			workingTable.remove(current.From);
		}
		return ret;
	}
	
	
	public static ArrayList<DijkstraResult> CalculateShortestPaths(Node source, double constant){
		ArrayList<DijkstraResult> ret = new ArrayList<>();
		HashSet<Node> settledNodes = new HashSet<>();
		HashMap<Node, DijkstraResult> workingTable = new HashMap<>();
		workingTable.put(source, new DijkstraResult(source, source, 0));
		settledNodes.add(source);
		
		while(workingTable.size() > 0){
			List<DijkstraResult> dij_list = new ArrayList<>( workingTable.values());
			Collections.sort(dij_list);
			DijkstraResult current = dij_list.get(0);
			ret.add(current);
			for(Map.Entry<Node, LinkState> n: current.From.getNeighbors().entrySet()){
				if (workingTable.containsKey(n.getKey())){
					DijkstraResult r = workingTable.get(n.getKey());
					double newWeight = current.Weight + constant;
					if(r.Weight > newWeight){
						r.NextHop = current.From;
						r.Weight = newWeight;
					}
				}
				else if (!settledNodes.contains(n.getKey())){
					workingTable.put(n.getKey(), new DijkstraResult(n.getKey(), current.From, current.Weight + constant));
					settledNodes.add(n.getKey());
				}
			}
			workingTable.remove(current.From);
		}
		return ret;
	}
	
	
	public static ArrayList<DijkstraResult> CalculateShortestPathsCostFunc(AccessPoint source){
		ArrayList<DijkstraResult> ret = new ArrayList<>();
		HashSet<Node> settledNodes = new HashSet<>();
		HashMap<Node, DijkstraResult> workingTable = new HashMap<>();
		workingTable.put(source, new DijkstraResult(source, source, 0));
		settledNodes.add(source);
		
		while(workingTable.size() > 0){
			List<DijkstraResult> dij_list = new ArrayList<>( workingTable.values());
			Collections.sort(dij_list);
			DijkstraResult current = dij_list.get(0);
			ret.add(current);
			for(Map.Entry<Node, LinkState> n: current.From.getNeighbors().entrySet()){
				if (workingTable.containsKey(n.getKey())){
					DijkstraResult r = workingTable.get(n.getKey());
					double newWeight = current.Weight + source.getDataRate();
					if(r.Weight > newWeight){
						r.NextHop = current.From;
						r.Weight = newWeight;
					}
				}
				else if (!settledNodes.contains(n.getKey())){
					workingTable.put(n.getKey(), new DijkstraResult(n.getKey(), current.From, current.Weight + source.getDataRate()));
					settledNodes.add(n.getKey());
				}
			}
			workingTable.remove(current.From);
		}
		return ret;
	} 
	

	
	
	public static void main(String[] args){
//		TestNode n1 = new TestNode("N1");
//		TestNode n2 = new TestNode("N2");
//		TestNode n3 = new TestNode("N3");
//		TestNode n4 = new TestNode("N4");
//		TestNode n5 = new TestNode("N5");
//		TestNode n6 = new TestNode("N6");
//		
//		Node.AddNodeLink(n1, n2, 7);
//        Node.AddNodeLink(n1, n3, 9);
//        Node.AddNodeLink(n1, n6, 14);
//        Node.AddNodeLink(n2, n3, 10);
//        Node.AddNodeLink(n2, n4, 15);
//        Node.AddNodeLink(n3, n4, 11);
//        Node.AddNodeLink(n3, n6, 2);
//        Node.AddNodeLink(n4, n5, 6);
//        Node.AddNodeLink(n5, n6, 9);
//        
//        for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(n1)){
//        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
//        }
		
		AccessPoint n1 = new AccessPoint("N1");
		AccessPoint n2 = new AccessPoint("N2");
		TestNode n3 = new TestNode("N3");
		TestNode n4 = new TestNode("N4");
		TestNode n5 = new TestNode("N5");
		n1.setDataRate(10);
		n2.setDataRate(20);
		
		Node.AddNodeLink(n1, n3, 0);
        Node.AddNodeLink(n3, n4, 0);
        Node.AddNodeLink(n2, n4, 0);
        Node.AddNodeLink(n4, n5, 0);
        Node.AddNodeLink(n3, n5, 0);
                
        for (DijkstraResult dij: Dijkstra.CalculateShortestPathsCostFunc(n1)){
        	System.out.printf("To %s: %s next hop = %s, weight = %f\n", n1.getName(), dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
        }
        
        for (DijkstraResult dij: Dijkstra.CalculateShortestPathsCostFunc(n2)){
        	System.out.printf("To %s: %s next hop = %s, weight = %f\n", n2.getName(), dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
        }
		
		
        	
	}
	
	private static class TestNode extends Node{

		public TestNode(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		
		
	}
	 

}
