package edu.rutgers.winlab.sim.examples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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

public class AggregationSimulationOnpath {
	protected static PrintStream outputFile(String name) throws FileNotFoundException {
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
	}

	public static void main(String[] args) throws IOException {
		if(args == null || args.length != 5) {
			System.err.println("Usage: java -jar *.jar <compute nodes topo> <aps trace folder> <# of aggregators> <timeout in second> <grid size>");
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
				int grid_size = Integer.parseInt(args[4]);
				TopoManager.initAPsfromMap(ap_map, cnodemap, grid_size, ap_filename);

				for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
					entry.getValue().setDataRate(10);
				}
				
				System.setOut(outputFile(args[3] + "_" + args[2] + "_resultTraceOnpath" + f.getName()));

				
				int k = Integer.parseInt(args[2]);
				
				if(k == 4) {
					for(int i = 0; i < 2; i++) {
						for(int j = 0; j < 2; j++) {
							String cname = "c" + (i * grid_size  + j);
							cnodemap.get(cname).setSelected(true);
						}
					}
				}else if(k == 8) {
					for(int i = 0; i < 3; i++) {
						for(int j = 0; j < 3; j++) {
							if( i != 2 && j != 2) {
								String cname = "c" + (i * grid_size + j);
								cnodemap.get(cname).setSelected(true);
							}	
						}
					}
					
				}else if(k == 16) {
					for(int i = 0; i < 4; i++) {
						for(int j = 0; j < 4; j++) {
							String cname = "c" + (i * grid_size  + j);
							cnodemap.get(cname).setSelected(true);
						}
					}
				}else if(k == 32) {
					for(int i = 0; i < 6; i++) {
						for(int j = 0; j < 6; j++) {
							if(i >= 4 && j >= 4)
								continue;
							String cname = "c" + (i * grid_size  + j);
							cnodemap.get(cname).setSelected(true);
						}
					}
				}else if(k ==64) {
					for(int i = 0; i < 8; i++) {
						for(int j = 0; j < 8; j++) {
							String cname = "c" + (i * grid_size  + j);
							cnodemap.get(cname).setSelected(true);
						}
					}
				}

				//Init the Server Node
				Server server_node = new Server("s1");
				Node.AddNodeLink(server_node, cnodemap.get("c0"), 0.01);
				
				//Init all CNodes
				for(Map.Entry<String, ComputeNode> entry: cnodemap.entrySet()) {
					entry.getValue().initNode();
					entry.getValue().setTimeout(Double.parseDouble(args[3]));
					MappingTable.Insert(entry.getValue(), server_node);
				}
				
				
				for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
					entry.getValue().setFirstANnode(server_node);
					entry.getValue().generateTrafficByID(1);
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
