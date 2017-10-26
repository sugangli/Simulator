package edu.rutgers.winlab.sim.aggregation;

import java.util.HashMap;

import edu.rutgers.winlab.sim.core.Node;

public class MappingTable {
	private static MappingTable _default = new MappingTable(); 
	private static HashMap<Node, Node> _mappingTable = new HashMap<>();
	
	public static void Insert(Node src, Node dst) {
		_default._insert(src, dst);
	}
	
	public void _insert(Node src, Node dst) {
		_mappingTable.putIfAbsent(src, dst);
		_mappingTable.put(src, dst);
	}
	
	public static Node getDestination(Node src) {
		return _default._getDestination(src);
	}
	
	public Node _getDestination(Node src) {
		return _mappingTable.get(src);
	}

}
