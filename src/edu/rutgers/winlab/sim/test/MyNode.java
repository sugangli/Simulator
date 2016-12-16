package edu.rutgers.winlab.sim.test;

import edu.rutgers.winlab.sim.core.Action;
import edu.rutgers.winlab.sim.core.EventQueue;
import edu.rutgers.winlab.sim.core.ISerializable;
import edu.rutgers.winlab.sim.core.ISerializableHelper;
import edu.rutgers.winlab.sim.core.MACPacket;
import edu.rutgers.winlab.sim.core.Node;
import edu.rutgers.winlab.sim.core.Payload;
import edu.rutgers.winlab.sim.core.Serial;


public class MyNode extends Node{
	
	private Serial.SerialAction<MACPacket> processPacket = new Serial.SerialAction<MACPacket>() {

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			MyData data = (MyData)parameter.getMacpayload();
			System.out.printf("Node=%s Now=%f Packet=%s%n", MyNode.this, EventQueue.Now(), parameter);
			if (data == null) return 0;
			if (data.getVal().equals("aa")){
				MACPacket new_packet = new MACPacket(MyNode.this, parameter.From, new MyData("BB"));
				s.AddEvent(new Serial.SerialAction<MACPacket>() {

					@Override
					public double execute(Serial<MACPacket> ser, MACPacket pkt) {
						sendPacket(pkt, false);
						return 0;
					}
				}, new_packet);
			}
			return 500 * EventQueue.MILLI_SECOND;
		}
	};

	public MyNode(String name) {
		super(name);
		setProcessPacket(processPacket);
	}
		
	
	
	public static class MyData extends Payload{
			
			public MyData(String val){
				super(val);
			}
			
			public String toString(){
				return String.format("MyData:{%s}", getVal());
			}
	
			@Override
			public int getSizeInBits() {
				
				return 125 * ISerializableHelper.KBIT / 2 - MACPacket.MAC_PACKET_HEADER_SIZE;
			}
			
		}
//
//	
//	public class MyProcessPacketAction extends ProcessPacketAction {
//		
//		@Override
//		public void setDelay(double delay) {
//			super.setDelay(delay);
//		}
//		@Override 
//		public void execute(Object...objects){
//			Node mynode = (Node) objects[0];
//			@SuppressWarnings("unchecked")
//			Serial<MACPacket> serial = (Serial<MACPacket>) objects[1];
//			MACPacket macpaket = (MACPacket) objects[2];
//			MyData data = (MyData) macpaket.getMacpayload();
//			if (data == null) return;
//			if (data.getVal().equals("aa")){
//				MACPacket new_packet = new MACPacket(mynode, macpaket.From, new MyData("BB"));
//				serial.AddEvent(new SendPacketAction(new_packet, false), new MACPacket(mynode, macpaket.From, new MyData("BB")));
//			}
//			setDelay(500 * EventQueue.MILLI_SECOND);
//		}
//		
//	}
	
	public static void main(String[] args){
		MyNode n1 = new MyNode("N1");
		MyNode n2 = new MyNode("N2");
		Node.AddNodeLink(n1, n2, 0);
		n1.sendPacket(new MACPacket(n1, n2, new MyData("aa")), false);
//		n2.sendPacket(new MACPacket(n2, n1, new MyData("aa")), false);
//		n1.sendPacket(new MACPacket(n1, n2, new MyData("aa")), false);
		EventQueue.Run();
	}

}
