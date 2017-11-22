package edu.rutgers.winlab.sim.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.omg.CORBA.OMGVMCID;

import edu.rutgers.winlab.sim.core.Action;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;

public class ComputeNode extends Node {
	
	

	private HashMap<String, ArrayList<String>> aggregateMap = new HashMap<String, ArrayList<String>>(); // <Aggregated Object ID, Time Window>
	private double timeout;
	private boolean selected = false;
	public boolean isSelected() {
		return selected;
	}

	public double getTimeout() {
		return timeout;
	}

	public void setTimeout(double timeout) {
		this.timeout = timeout;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private HashMap<String, HashSet<Node>> routingTable;
	public ComputeNode(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		routingTable = new HashMap<>();
	}
	
	public ComputeNode(String name, double timeout) {
		super(name);
		this.timeout = timeout;
		routingTable = new HashMap<>();
	}
	
	public void initNode() {
		if(selected) {
			setProcessPacket(aggregatePacket);
		}else {
			setProcessPacket(routingPacket);
		}
			
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
	
	public static class AggregatedData extends Payload{
		// Format: {{Object_ID},{Data}}
		public AggregatedData(String val) {
			super(val);
			// TODO Auto-generated constructor stub
		}
		public String getSrc() {
			String[] sc = getVal().split(",");
			return sc[0];
		}
		public String getDst() {
			String[] sc = getVal().split(",");
			return sc[1];
		}
		public String getObjectID() {
			String[] sc = getVal().split(",");
			return sc[2];
		}
		
		public String getData() {
			String[] sc = getVal().split(",");
			return sc[3];
			
		}
		
		public String toString(){
			return String.format("ArregatedData:{%s}", getVal());
		}

		@Override
		public long getSizeInBits() {
			
			return 1454 * ISerializableHelper.BYTE/8  - MACPacket.MAC_PACKET_HEADER_SIZE;
		}
		
		
	}
	
	private String Compute(ArrayList<String> datalist) {
		return datalist.get(0);
	}
	
	private Action wait_n_send = new Action() {

		@Override
		public void execute(Object... args) {
			// TODO Auto-generated method stub
			String oid = (String) args[0];
			ArrayList<String> datalist = aggregateMap.get(oid);
			String newdata = Compute(datalist);
			String dst = MappingTable.getDestination(ComputeNode.this).getName();
			HashSet<Node> nextHops = ComputeNode.this.routingTable.get(dst);
			if(nextHops == null || nextHops.size() == 0) {
				System.err.println("Error: No next hop found for this Compute Node" + ComputeNode.this.getName());
			}
			for(Node n: nextHops) {
				MACPacket new_packet = new MACPacket(ComputeNode.this, n, new AggregatedData(ComputeNode.this.getName() + "," + dst + "," + oid + "," + newdata));;
				ComputeNode.this.sendPacket(new_packet, false);
			}
			
			aggregateMap.remove(oid);
		}
		
	};
	
	private Serial.SerialAction<MACPacket> aggregatePacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			// TODO Auto-generated method stub
			AggregatedData a_data = (AggregatedData) parameter.getMacpayload();
			String oid = a_data.getObjectID();
			System.out.printf("Now=%f Node=%s Packet=%s%n", EventQueue.Now(), ComputeNode.this, parameter);
			if(!aggregateMap.containsKey(oid)) {
				ArrayList<String> list = new ArrayList<>();
				list.add(a_data.getData());
				aggregateMap.put(oid, list);
				EventQueue.AddEvent(EventQueue.Now() + timeout, wait_n_send, oid);
			}else {
				aggregateMap.get(oid).add(a_data.getData());
			}
			
			return 0;
		}};
	
		private Serial.SerialAction<MACPacket> routingPacket = new Serial.SerialAction<MACPacket>() {

			@Override
			public double execute(Serial<MACPacket> s, MACPacket parameter) {
				// TODO Auto-generated method stub
				System.out.printf("Now=%f Node=%s Packet=%s%n", EventQueue.Now(), ComputeNode.this, parameter);
				AggregatedData payload = (AggregatedData) parameter.getMacpayload();
				String dst = payload.getDst();
				HashSet<Node> nextHops = ComputeNode.this.routingTable.get(dst);
				if(nextHops != null) {

					for(Node n:nextHops) {
						MACPacket newpacket = new MACPacket(ComputeNode.this, n, payload);
						ComputeNode.this.sendPacket(newpacket, false);
					}
				}else {
					System.err.printf("Error: No next hop found at Node=%s", ComputeNode.this);
				}
				return 0;
			}
			
		};

}
