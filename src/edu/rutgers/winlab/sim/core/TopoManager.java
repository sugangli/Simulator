package edu.rutgers.winlab.sim.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import edu.rutgers.winlab.sim.aggregation.AccessPoint;
import edu.rutgers.winlab.sim.aggregation.ComputeNode;
import edu.rutgers.winlab.sim.aggregation.Router;

public class TopoManager {
	/*
	 * Topo File Format:
	 * <Node1Name> <BW1> <Node2Name> <BW2> <Latency>
	 */
	public static void initRouters(HashMap<String, Router> nodemap, String inputfile) throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while((line = br.readLine()) != null) {
			String[] parts  = line.split("\t");
			if(!nodemap.containsKey(parts[0])){
				Router r = new Router(parts[0]);
				r.setBandwidth_MBPS(Double.parseDouble(parts[1]));
				nodemap.put(parts[0], r);
			}
			
			if(!nodemap.containsKey(parts[2])){
				Router r = new Router(parts[2]);
				r.setBandwidth_MBPS(Double.parseDouble(parts[3]));
				nodemap.put(parts[2], r);
			}
			
			if(nodemap.containsKey(parts[0]) && nodemap.containsKey(parts[2])) {
				Node.AddNodeLink(nodemap.get(parts[0]), nodemap.get(parts[2]), Double.parseDouble(parts[4]));
			}
			
			
		}
		
		br.close();
	}
	
	public static void initComputeNodes(HashMap<String, ComputeNode> nodemap, String inputfile) throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while((line = br.readLine()) != null) {
			String[] parts  = line.split("\t");
			if(!nodemap.containsKey(parts[0])){
				ComputeNode r = new ComputeNode(parts[0], 1);
				r.setBandwidth_MBPS(Double.parseDouble(parts[1]));
				nodemap.put(parts[0], r);
			}
			
			if(!nodemap.containsKey(parts[2])){
				ComputeNode r = new ComputeNode(parts[2], 1);
				r.setBandwidth_MBPS(Double.parseDouble(parts[3]));
				nodemap.put(parts[2], r);
			}
			
			if(nodemap.containsKey(parts[0]) && nodemap.containsKey(parts[2])) {
				Node.AddNodeLink(nodemap.get(parts[0]), nodemap.get(parts[2]), Double.parseDouble(parts[4]));
			}
			
			
		}
		
		br.close();
	}
	
	
	public static void initAPs(HashMap<String, AccessPoint> ap_map, HashMap<String, ComputeNode> cnodemap, String inputfile) throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while((line = br.readLine()) != null) {
			String[] parts  = line.split("\t");
			if(!ap_map.containsKey(parts[0])){
				AccessPoint r = new AccessPoint(parts[0]);
				r.setBandwidth_MBPS(Double.parseDouble(parts[1]));
				ap_map.put(parts[0], r);
			}
			
			if(!cnodemap.containsKey(parts[2])){
				ComputeNode r = new ComputeNode(parts[2]);
				r.setBandwidth_MBPS(Double.parseDouble(parts[3]));
				cnodemap.put(parts[2], r);
			}
			
			if(ap_map.containsKey(parts[0]) && cnodemap.containsKey(parts[2])) {
				Node.AddNodeLink(ap_map.get(parts[0]), cnodemap.get(parts[2]), Double.parseDouble(parts[4]));
			}
			
			
		}
		
		br.close();
	}
	
	public static void initAPsfromMap(HashMap<String, AccessPoint> ap_map, HashMap<String, ComputeNode> cnodemap, int grid_size, String inputfile) throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while((line = br.readLine()) != null) {
			String[] parts  = line.split("\t");
			if(!ap_map.containsKey("ap"+parts[0])){
				AccessPoint r = new AccessPoint("ap" + parts[0]);
				r.setBandwidth_MBPS(Double.parseDouble(parts[1]));
				r.setDataRate(Double.parseDouble(parts[2]));
				ap_map.put(parts[0], r);
			}
			String[] cor = parts[3].split(",");
			int index = Integer.parseInt(cor[0]) * grid_size + Integer.parseInt(cor[1]);
			String c_name = "c"+index;
			if(!cnodemap.containsKey(c_name)){
				ComputeNode r = new ComputeNode(c_name);
				r.setBandwidth_MBPS(1500);
				cnodemap.put(c_name, r);
			}
			
			if(ap_map.containsKey(parts[0]) && cnodemap.containsKey(c_name)) {
				Node.AddNodeLink(ap_map.get(parts[0]), cnodemap.get(c_name), 0.01);
			}
			
			
		}
		
		br.close();
	}

}
