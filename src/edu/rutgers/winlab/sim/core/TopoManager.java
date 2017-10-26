package edu.rutgers.winlab.sim.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
			
			if(nodemap.containsKey(parts[0]) && nodemap.containsKey(parts[0])) {
				Node.AddNodeLink(nodemap.get(parts[0]), nodemap.get(parts[2]), Double.parseDouble(parts[4]));
			}
			
			
		}
		
		br.close();
	}

}
