package edu.rutgers.winlab.sim.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;


import edu.rutgers.winlab.sim.aggregation.AccessPoint;
import edu.rutgers.winlab.sim.aggregation.ComputeNode;
import edu.rutgers.winlab.sim.aggregation.Dijkstra;
import edu.rutgers.winlab.sim.aggregation.Dijkstra.DijkstraResult;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.TopoManager;

public class KMediods {

	static HashMap<ComputeNode, ArrayList<AccessPoint>> assignmentmap = new HashMap<>();
	static HashMap<AccessPoint, ArrayList<DijkstraResult>> pathmap = new HashMap<>();
	static int CONVERGE_COUNT = 5;
	
	public static void AssignFirstHopsRandom(HashMap<String, AccessPoint> ap_map, HashMap<String, ComputeNode> cnode_map, int k, boolean isGlobal, int num_iteration){
		LinkedList<ComputeNode> ret = new LinkedList<>(); 
		if(k >= cnode_map.size()) {
			System.err.println("Error: K value is larger than the size of compute node pool!");
		}
		ArrayList<Integer> index_list = new ArrayList<>();
		for(int i = 0; i < cnode_map.size(); i++) {
			index_list.add(i);
		}
		Collections.shuffle(index_list, new Random(System.currentTimeMillis()));
		for(int i = 0; i < k; i++) {
			cnode_map.get("c"+index_list.get(i)).setSelected(true);
			ret.add(cnode_map.get("c"+index_list.get(i)));
//			System.out.println("Init CNode:c" + index_list.get(i));
		}

		for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
			//			System.out.println("Ap:" + entry.getValue().getName());
			ArrayList<DijkstraResult> results =  Dijkstra.CalculateShortestPathsCostFunc(entry.getValue());
			double min_weight = Integer.MAX_VALUE;
			ComputeNode closest_node = null;
			for(DijkstraResult dij: results) {
				if(dij.From.getName().startsWith("c")) {
					ComputeNode cn = (ComputeNode)dij.From;
					if(cn.isSelected()) {
						if(dij.Weight < min_weight) {
							min_weight = dij.Weight;
							closest_node = cn;
							//							System.out.println("CNode:" + cn.getName() + " Ap:" + entry.getValue().getName());
						} 
					}
				}	
			}
			if(closest_node != null) {
				assignmentmap.putIfAbsent(closest_node, new ArrayList<>());
				assignmentmap.get(closest_node).add(entry.getValue());
			}

			pathmap.put(entry.getValue(), results);	
		}
		
	}
	
	public static LinkedList<ComputeNode> AssignFirstHops(HashMap<String, AccessPoint> ap_map, HashMap<String, ComputeNode> cnode_map, int k, boolean isGlobal, int num_iteration) {
		LinkedList<ComputeNode> ret = new LinkedList<>(); 
		
		if(k >= cnode_map.size()) {
			System.err.println("Error: K value is larger than the size of compute node pool!");
		}
		ArrayList<Integer> index_list = new ArrayList<>();
		for(int i = 0; i < cnode_map.size(); i++) {
			index_list.add(i);
		}
		Collections.shuffle(index_list, new Random(System.currentTimeMillis()));
		for(int i = 0; i < k; i++) {
			cnode_map.get("c"+index_list.get(i)).setSelected(true);
			ret.add(cnode_map.get("c"+index_list.get(i)));
//			System.out.println("Init CNode:c" + index_list.get(i));
		}

		for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
			//			System.out.println("Ap:" + entry.getValue().getName());
			ArrayList<DijkstraResult> results =  Dijkstra.CalculateShortestPathsCostFunc(entry.getValue());
			double min_weight = Integer.MAX_VALUE;
			ComputeNode closest_node = null;
			for(DijkstraResult dij: results) {
				if(dij.From.getName().startsWith("c")) {
					ComputeNode cn = (ComputeNode)dij.From;
					if(cn.isSelected()) {
						if(dij.Weight < min_weight) {
							min_weight = dij.Weight;
							closest_node = cn;
							//							System.out.println("CNode:" + cn.getName() + " Ap:" + entry.getValue().getName());
						} 
					}
				}	
			}
			if(closest_node != null) {
				assignmentmap.putIfAbsent(closest_node, new ArrayList<>());
				assignmentmap.get(closest_node).add(entry.getValue());
			}

			pathmap.put(entry.getValue(), results);	
		}


		//Iteratively search APs' neighbor and update
		int num = num_iteration;
		double last_sum = Double.MAX_VALUE;
		int count = 0;
		HashMap<ComputeNode, ArrayList<AccessPoint>> temp_assign_map = new HashMap<>();
		for(int i = 0; i < num; i++) {
			int size = ret.size();
			double pre_sum = last_sum;
			for(int j = 0; j < size; j++) {
				ComputeNode cn = ret.peek();
				double min_sum = Double.MAX_VALUE;
				ComputeNode nextnode = null;
				if(isGlobal) {
					for(Map.Entry<String, ComputeNode> otherentry: cnode_map.entrySet()) {
						ComputeNode temp = otherentry.getValue();
						if(!temp.isSelected()) {
							temp_assign_map.clear();
							double sum = CalculateTotalCost(cn, temp, ap_map, pathmap, temp_assign_map);
							if(sum <= min_sum) {
								//							System.out.println("Cur Node:" + cn.getName() + " Temp:" + temp.getName() + " Sum:" + sum);
								min_sum = sum;
								cn.setSelected(false);
								nextnode = temp;
								nextnode.setSelected(true);
								cn = nextnode;
								assignmentmap = (HashMap<ComputeNode, ArrayList<AccessPoint>>)temp_assign_map.clone();
							}
						}
					}
				}else {
					for(Node neighbor: cn.getNeighbors().keySet()) {
						if(neighbor.getName().startsWith("c")) {
							ComputeNode temp = (ComputeNode) neighbor;
							if(!temp.isSelected()) {
								temp_assign_map.clear();
								double sum = CalculateTotalCost(cn, temp, ap_map, pathmap, temp_assign_map);
								if(sum <= min_sum) {
									//								System.out.println("Cur Node:" + cn.getName() + " Temp:" + temp.getName() + " Sum:" + sum);
									min_sum = sum;
									cn.setSelected(false);
									nextnode = temp;
									nextnode.setSelected(true);
									cn = nextnode;
									assignmentmap = (HashMap<ComputeNode, ArrayList<AccessPoint>>)temp_assign_map.clone();
								}
							}

						}

					}
				}

				
				if(nextnode != null) {
					if(last_sum >= min_sum)
						last_sum = min_sum;
					ret.poll();
					ret.offer(nextnode);
				}	
			}
			System.err.println("Iter " + i + " " + last_sum);

			if(last_sum == pre_sum) {
				count++;
			}else {
				count = 0;
			}
			if(count == CONVERGE_COUNT)
				break;	
		}
		return ret;

	}
	
	public static void clear() {
		pathmap.clear();
		assignmentmap.clear();
	}
	public static HashMap<AccessPoint, ArrayList<DijkstraResult>> getPathmap() {
		return pathmap;
	}

	public static void setPathmap(HashMap<AccessPoint, ArrayList<DijkstraResult>> pathmap) {
		KMediods.pathmap = pathmap;
	}

	public static HashMap<ComputeNode, ArrayList<AccessPoint>> getAssignmentmap() {
		return assignmentmap;
	}
	public static void setAssignmentmap(HashMap<ComputeNode, ArrayList<AccessPoint>> assignmentmap) {
		KMediods.assignmentmap = assignmentmap;
	}
	private static double CalculateTotalCost(ComputeNode cur_node, ComputeNode candidate, HashMap<String, AccessPoint> ap_map, HashMap<AccessPoint, ArrayList<DijkstraResult>> pathmap, HashMap<ComputeNode, ArrayList<AccessPoint>> temp_assign_map) {
		double sum = 0;
		for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
			ArrayList<DijkstraResult> results =  pathmap.get(entry.getValue());
			double min_weight = Integer.MAX_VALUE;
			ComputeNode closest_node = null;
			for(DijkstraResult dij: results) {
				if(dij.From.getName().startsWith("c")) {
					ComputeNode cn = (ComputeNode)dij.From;
					if((cn.isSelected() && cn.getName() != cur_node.getName()) || cn.getName() == candidate.getName()) {
						if(dij.Weight <= min_weight) {
							min_weight = dij.Weight;
							closest_node = cn;
						} 
					}
				}	
			}

			if(closest_node != null) {
				temp_assign_map.putIfAbsent(closest_node, new ArrayList<>());
				temp_assign_map.get(closest_node).add(entry.getValue());
				sum += min_weight;
			}
		}
		return sum;
	}
	public static void main(String[] args) {
		String filename = "data/topo_20x20.txt";
		HashMap<String, ComputeNode> map = new HashMap<>();
		String filename2 = "data/ap_topo_6_per_node_20x20.txt";
		HashMap<String, AccessPoint> ap_map = new HashMap<>();
		try {
			TopoManager.initComputeNodes(map, filename);
			TopoManager.initAPs(ap_map, map, filename2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(Map.Entry<String, AccessPoint> entry: ap_map.entrySet()) {
			entry.getValue().setDataRate(10);
		}

		LinkedList<ComputeNode> ret = KMediods.AssignFirstHops(ap_map, map, 4, false, 40);
		int r = 20;
		int c = 20;
		HashSet<String> ret_set = new HashSet<>();
		for(ComputeNode cn: ret) {
			System.out.println("Selected CNode:" + cn.getName());
			ret_set.add(cn.getName());
		}
		
		for(Map.Entry<ComputeNode, ArrayList<AccessPoint>> entry: KMediods.getAssignmentmap().entrySet()) {
			System.out.println("CNode:" + entry.getKey() + " Size: " + entry.getValue().size() + " Assigned AP:" + Arrays.toString(entry.getValue().toArray()));
		}
		
		for(int i = 0; i < r; i++) {
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < c; j++) {
				int index = i * c + j;
				String name = "c" + index;
				if(ret_set.contains(name)) {
					sb.append("*");
				}else {
					sb.append("#");
				}

			}
			System.out.println(sb.toString());
		}

	}


}