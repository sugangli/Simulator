package edu.rutgers.winlab.sim.examples;

import edu.rutgers.winlab.sim.aggregation.ComputeNode;
import edu.rutgers.winlab.sim.aggregation.ComputeNode.AggregatedData;
import edu.rutgers.winlab.sim.aggregation.MappingTable;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;
import edu.rutgers.winlab.sim.examples.MyNode.MyData;

public class AggregationExample {
	
	
	public static class TestNode extends Node{
		
		private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

			@Override
			public double execute(Serial<MACPacket> s, MACPacket parameter) {
				AggregatedData data = (AggregatedData)parameter.getMacpayload();
				System.out.printf("Node=%s Now=%f Packet=%s%n", TestNode.this, EventQueue.Now(), parameter);
				if (data == null) return 0;
				return 0;
			}
		};

		public TestNode(String name) {
			super(name);
			setProcessPacket(processPacket);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class TestData extends Payload{
		
		public TestData(String val){
			super(val);
		}
		
		public String toString(){
			return String.format("TestData:{%s}", getVal());
		}

		@Override
		public long getSizeInBits() {
			
			return 1454 * ISerializableHelper.BYTE/8  - MACPacket.MAC_PACKET_HEADER_SIZE;
		}
		
}
	public static void main(String[] args) {
		TestNode n1 = new TestNode("n1");
		TestNode n2 = new TestNode("n2");
		ComputeNode a1 = new ComputeNode("a1", 0.5 * EventQueue.MILLI_SECOND);
		a1.setSelected(true);
		a1.initNode();
		TestNode n3 = new TestNode("n3");
		
		Node.AddNodeLink(n1, a1, 0);
		Node.AddNodeLink(n2, a1, 0);
		Node.AddNodeLink(a1, n3, 0);
		
		MappingTable.Insert(a1, n3);
		
		for(int i = 0; i < 1; i++) {
			n1.sendPacket(new MACPacket(n1, a1, new ComputeNode.AggregatedData(n1.getName() + "," + a1.getName() + ",1,100")), false);
		}
		
		for(int i = 0; i < 1; i++) {
			n2.sendPacket(new MACPacket(n2, a1, new ComputeNode.AggregatedData(n2.getName() + "," + a1.getName() + ",1,100")), false);
		}
		EventQueue.Run();
		return;
	}

}
