package edu.rutgers.winlab.sim.examples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.winlab.sim.aggregation.AccessPoint;
import edu.rutgers.winlab.sim.aggregation.ComputeNode;
import edu.rutgers.winlab.sim.aggregation.Dijkstra;
import edu.rutgers.winlab.sim.aggregation.MappingTable;
import edu.rutgers.winlab.sim.aggregation.Server;
import edu.rutgers.winlab.sim.aggregation.Dijkstra.DijkstraResult;
import edu.rutgers.winlab.sim.algorithm.KMediods;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.TopoManager;

public class AggregationSimulationRandom {
	protected static PrintStream outputFile(String name) throws FileNotFoundException {
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
	}

	public static void main(String[] args) throws IOException {
		if(args == null || args.length != 4) {
			System.err.println("Usage: java -jar *.jar <compute nodes topo> <aps trace folder> <# of aggregators> <timeout in second>");
		}else {

			String filename = args[0];
			File tracefolder = new File(args[1]);
			File[] listoffiles = tracefolder.listFiles();
			for(File f: listoffiles) {
				HashMap<String, ComputeNode> cnodemap = new HashMap<>();
				TopoManager.initComputeNodes(cnodemap, filename);
				String ap_filename = args[1] + "/" + f.getName();
				System.err.printf("Trace File = %s # of Aggr = %s%n", ap_filename, args[2]);
				HashMap<String, AccessPoint> ap_map = new HashMap<>();
				TopoManager.initAPsfromMap(ap_map, cnodemap, 20, ap_filename);

				for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
					entry.getValue().setDataRate(10);
				}
				System.setOut(outputFile(args[3] + "_" +args[2] + "_resultTraceRandom" + f.getName()));
				
				KMediods.AssignFirstHopsRandom(ap_map, cnodemap, Integer.parseInt(args[2]), false, 40);

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
					entry.getKey().setTimeout(Double.parseDouble(args[3]));
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
				KMediods.clear();
			}

		}


	}
}
