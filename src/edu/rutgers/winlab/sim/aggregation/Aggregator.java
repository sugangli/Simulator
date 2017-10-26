package edu.rutgers.winlab.sim.aggregation;

import java.util.ArrayList;
import java.util.HashMap;

import edu.rutgers.winlab.sim.core.Action;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;
import edu.rutgers.winlab.sim.examples.MyNode;
import edu.rutgers.winlab.sim.examples.AggregationExample.TestNode;
import edu.rutgers.winlab.sim.examples.MyNode.MyData;

public class Aggregator extends Node {
	
	

	private HashMap<String, ArrayList<String>> aggregateMap = new HashMap<String, ArrayList<String>>(); // <Aggregated Object ID, Time Window>
	private double timeout;
	public Aggregator(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public Aggregator(String name, double timeout) {
		super(name);
		this.timeout = timeout;
		setProcessPacket(aggregatePacket);
	}
	
	public static class AggregatedData extends Payload{
		// Format: {{Object_ID},{Data}}
		public AggregatedData(String val) {
			super(val);
			// TODO Auto-generated constructor stub
		}
		
		public String getObjectID() {
			String[] sc = getVal().split(",");
			return sc[0];
		}
		
		public String getData() {
			String[] sc = getVal().split(",");
			return sc[1];
			
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
			MACPacket new_packet = new MACPacket(Aggregator.this, MappingTable.getDestination(Aggregator.this), new AggregatedData(newdata));;
			Aggregator.this.sendPacket(new_packet, false);
			aggregateMap.remove(oid);
		}
		
	};
	
	private Serial.SerialAction<MACPacket> aggregatePacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			// TODO Auto-generated method stub
			AggregatedData a_data = (AggregatedData) parameter.getMacpayload();
			String oid = a_data.getObjectID();
//			System.out.printf("Node=%s Now=%f Packet=%s%n", Aggregator.this, EventQueue.Now(), parameter);
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

}
