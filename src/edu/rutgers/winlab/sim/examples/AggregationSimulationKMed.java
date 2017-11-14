package edu.rutgers.winlab.sim.examples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import edu.rutgers.winlab.sim.aggregation.AccessPoint;
import edu.rutgers.winlab.sim.aggregation.ComputeNode;
import edu.rutgers.winlab.sim.aggregation.Dijkstra;
import edu.rutgers.winlab.sim.aggregation.Router;
import edu.rutgers.winlab.sim.aggregation.Server;
import edu.rutgers.winlab.sim.aggregation.Dijkstra.DijkstraResult;
import edu.rutgers.winlab.sim.aggregation.MappingTable;
import edu.rutgers.winlab.sim.algorithm.KMediods;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.TopoManager;

public class AggregationSimulationKMed {
	protected static PrintStream outputFile(String name) throws FileNotFoundException {
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
	}

	public static void main(String[] args) throws IOException {
		if(args == null || args.length != 3) {
			System.err.println("Usage: java -jar *.jar <compute nodes topo> <aps trace folder> <# of aggregators>");
		}else {

			String filename = args[0];
			HashMap<String, ComputeNode> cnodemap = new HashMap<>();
			TopoManager.initComputeNodes(cnodemap, filename);


			File tracefolder = new File(args[1]);
			File[] listoffiles = tracefolder.listFiles();
			for(File f: listoffiles) {
				String ap_filename = args[1] + "\\" + f.getName();
				System.err.printf("Trace File = %s # of Aggr = %s%n", ap_filename, args[2]);
				HashMap<String, AccessPoint> ap_map = new HashMap<>();
				TopoManager.initAPsfromMap(ap_map, cnodemap, 20, ap_filename);

				for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
					entry.getValue().setDataRate(10);
				}
				System.setOut(outputFile("resultTrace" + f.getName()));
				
				KMediods.AssignFirstHops(ap_map, cnodemap, Integer.parseInt(args[2]), false, 40);

				//Init the Server Node
				Server server_node = new Server("s1");
				Node.AddNodeLink(server_node, cnodemap.get("c0"), 0.01);
				//Init all CNodes
				for(Map.Entry<String, ComputeNode> entry: cnodemap.entrySet()) {
					entry.getValue().initNode();
					MappingTable.Insert(entry.getValue(), server_node);
				}
				//insert routing table for route to aggregation points
				for(Map.Entry<ComputeNode, ArrayList<AccessPoint>> entry: KMediods.getAssignmentmap().entrySet()) {
					entry.getKey().initNode();
					entry.getKey().setTimeout(0.5);
					for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(entry.getKey())){
						//		        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
						ComputeNode computenode;
						if(dij.From.getName().startsWith("s") || dij.From.getName().startsWith("ap")) {
							continue;
						}else{
							computenode = (ComputeNode) dij.From;
							computenode.insertRoutingTable(entry.getKey().getName(), dij.NextHop);	
						}	
					}
					//Set up APs
					for(AccessPoint ap: entry.getValue()) {
						ap.setFirstANnode(entry.getKey());
						ap.generateTrafficByID(1);
					}
				}

				//insert routing table for routes to server
				for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(server_node)){
					//	        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
					ComputeNode computenode;
					if(dij.From.getName().startsWith("s") || dij.From.getName().startsWith("ap")) {
						continue;
					}else{
						computenode = (ComputeNode) dij.From;
						computenode.insertRoutingTable(server_node.getName(), dij.NextHop);	
					}	
				}


				EventQueue.RunSeconds(60);
				EventQueue.Reset();
			}


			//			String filename2 = args[1];
			//			try {
			//				System.setOut(outputFile(args[1]));
			//			} catch (FileNotFoundException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			//			HashMap<String, AccessPoint> ap_map = new HashMap<>();
			//			try {
			//				TopoManager.initComputeNodes(cnodemap, filename);
			//				TopoManager.initAPsfromMap(ap_map, cnodemap, 20, filename2);
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//
			//			for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
			//				entry.getValue().setDataRate(10);
			//			}

			
//						LinkedList<ComputeNode> selected_cnodes = KMediods.AssignFirstHops(ap_map, cnodemap, 64, false, 40);
						/***Visualization purpose***/
			//			
			//			int r = 20;
			//			int c = 20;
			//			HashSet<String> ret_set = new HashSet<>();
			//			for(ComputeNode cn: selected_cnodes) {
			//				System.out.println("Selected CNode:" + cn.getName());
			//				ret_set.add(cn.getName());
			//			}
			//			
			//			for(Map.Entry<ComputeNode, ArrayList<AccessPoint>> entry: KMediods.getAssignmentmap().entrySet()) {
			//				System.out.println("CNode:" + entry.getKey() + " Size: " + entry.getValue().size() + " Assigned AP:" + Arrays.toString(entry.getValue().toArray()));
			//			}
			//			
			//			for(int i = 0; i < r; i++) {
			//				StringBuilder sb = new StringBuilder();
			//				for(int j = 0; j < c; j++) {
			//					int index = i * c + j;
			//					String name = "c" + index;
			//					if(ret_set.contains(name)) {
			//						sb.append("*");
			//					}else {
			//						sb.append("#");
			//					}
			//
			//				}
			//				System.out.println(sb.toString());
			//			}
			/***
			 * Visualization Purpose
			 */




			//			//Init the Server Node
			//			Server server_node = new Server("s1");
			//			Node.AddNodeLink(server_node, cnodemap.get("c0"), 0.01);
			//			//Init all CNodes
			//			for(Map.Entry<String, ComputeNode> entry: cnodemap.entrySet()) {
			//				entry.getValue().initNode();
			//				MappingTable.Insert(entry.getValue(), server_node);
			//			}
			//			//insert routing table for route to aggregation points
			//			for(Map.Entry<ComputeNode, ArrayList<AccessPoint>> entry: KMediods.getAssignmentmap().entrySet()) {
			//				entry.getKey().initNode();
			//				entry.getKey().setTimeout(0.5);
			//				for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(entry.getKey())){
			//					//		        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
			//					ComputeNode computenode;
			//					if(dij.From.getName().startsWith("s") || dij.From.getName().startsWith("ap")) {
			//						continue;
			//					}else{
			//						computenode = (ComputeNode) dij.From;
			//						computenode.insertRoutingTable(entry.getKey().getName(), dij.NextHop);	
			//					}	
			//				}
			//				//Set up APs
			//				for(AccessPoint ap: entry.getValue()) {
			//					ap.setFirstANnode(entry.getKey());
			//					ap.generateTrafficByID(1);
			//				}
			//			}
			//
			//			//insert routing table for routes to server
			//			for (DijkstraResult dij: Dijkstra.CalculateShortestPaths(server_node)){
			//				//	        	System.out.printf("%s next hop = %s, weight = %f\n", dij.From.toString(), dij.NextHop.toString(), dij.Weight);;
			//				ComputeNode computenode;
			//				if(dij.From.getName().startsWith("s") || dij.From.getName().startsWith("ap")) {
			//					continue;
			//				}else{
			//					computenode = (ComputeNode) dij.From;
			//					computenode.insertRoutingTable(server_node.getName(), dij.NextHop);	
			//				}	
			//			}
			//
			//
			//			EventQueue.RunSeconds(30);
		}


	}



}
