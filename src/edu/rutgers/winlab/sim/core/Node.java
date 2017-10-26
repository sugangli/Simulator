package edu.rutgers.winlab.sim.core;

import java.util.HashMap;

public abstract class Node {
	
	private HashMap<Node, LinkState> Neighbors = new HashMap<Node, LinkState>();
	public HashMap<Node, LinkState> getNeighbors() {
		return Neighbors;
	}

	public void setNeighbors(HashMap<Node, LinkState> neighbors) {
		Neighbors = neighbors;
	}
	private String Name;
	private double Bandwidth_MBPS = 1500;
	public double getBandwidth_MBPS() {
		return Bandwidth_MBPS;
	}

	public void setBandwidth_MBPS(double bandwidth_MBPS) {
		Bandwidth_MBPS = bandwidth_MBPS;
	}
	private EventHandlerQueue<MACPacket> incomingQueue;
	private EventHandlerQueue<MACPacket> outgoingQueue;
	private Serial.SerialAction<MACPacket> processPacket;
	private double innerProcessDelay = EventQueue.MICRO_SECOND;
	
	public Node(String name){
		Name = name;
		incomingQueue = new  EventHandlerQueue<MACPacket>(new FIFOQueue<MACPacket>(Integer.MAX_VALUE),  new innerProcessPacketAction());
		outgoingQueue = new  EventHandlerQueue<MACPacket>(new FIFOQueue<MACPacket>(Integer.MAX_VALUE),  new innerSendPacketAction());
	}
	
	protected void setProcessPacket(Serial.SerialAction<MACPacket> processPacket) {
		this.processPacket = processPacket;
	}
	
	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}
	
	public String toString(){
		return String.format("Node:{Name:%s}", Name);
	}
	
	public static class LinkState {
		private Node CurrentNode;
		private Node TargetNode;
		private double Delay;
		private boolean Expired;
		
		
		public LinkState(Node currentNode, Node targetNode, double delay){
			
			this.CurrentNode = currentNode;
			this.TargetNode = targetNode;
			this.Delay = delay;
			this.Expired = false;
			
		}

		
		
		public boolean isExpired() {
			return Expired;
		}

		public void setExpired(boolean expired) {
			Expired = expired;
		}

		
		public Node getCurrentNode() {
			return CurrentNode;
		}

		public void setCurrentNode(Node currentNode) {
			CurrentNode = currentNode;
		}

		public Node getTargetNode() {
			return TargetNode;
		}

		public void setTargetNode(Node targetNode) {
			TargetNode = targetNode;
		}

		public double getDelay() {
			return Delay;
		}

		public void setDelay(double delay) {
			Delay = delay;
		};
		
		
		
	}
	
	public static void AddNodeLink(Node n1, Node n2, double delay){
		
		if(!n1.Neighbors.containsKey(n2)){
			
			n1.Neighbors.put(n2, new LinkState(n1, n2, delay));
			n2.Neighbors.put(n1, new LinkState(n2, n1, delay));
			
		}
		
	}
	
	public void RemoveNodeLink(Node n1, Node n2){
		
		if(n1.Neighbors.containsKey(n2)){
	
			n1.Neighbors.remove(n2);
			n2.Neighbors.remove(n1);
			
		}
		
	}
	
	public class innerProcessPacketAction implements Serial.SerialAction<MACPacket>{
		
		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			if (parameter.To == Node.this || parameter == null ){
				s.AddEvent(processPacket,  parameter);
			}
			return innerProcessDelay;
		}
	}
	
	public class innerSendPacketAction implements Serial.SerialAction<MACPacket>{
		

		@Override
		public double execute(Serial<MACPacket> s, MACPacket parameter) {
			double sendTime = GetSendTimeInSeconds(parameter); 
			double receiveTime = EventQueue.Now() + sendTime;
			if(parameter.To == null){
				for(HashMap.Entry<Node, LinkState> entry: Neighbors.entrySet()){
//					System.out.println("innerSendPacketAction:EventQueue.AddEvent.EnqueueIncomingPacketAction");
					EventQueue.AddEvent(receiveTime + entry.getValue().getDelay(), 
							entry.getKey().enqueueIncomingPacketAction, parameter);
				}
			}else{
				LinkState ls = Neighbors.get(parameter.To);
//				System.out.println("innerSendPacketAction:EventQueue.AddEvent.EnqueueIncomingPacketAction");
				EventQueue.AddEvent(receiveTime + ls.Delay, parameter.To.enqueueIncomingPacketAction, parameter);
			}
			return sendTime;
		}
		
		
		
	}
	
	public void EnqueueIncomingPacket(MACPacket macpacket){
		incomingQueue.Enqueue(macpacket, false);
	}
	
	public void sendPacket(MACPacket packet, boolean isPrioritized) {
//		System.out.println("Node.sendPacket.outgoingQueue.Enqueue");
		outgoingQueue.Enqueue(packet, isPrioritized);
	}
	
	public double ProcessPacket(MACPacket macpacket){
		return 0;
	}
	
	public double GetSendTimeInSeconds(ISerializable packet){
		return packet.getSizeInBits() / Bandwidth_MBPS / ISerializableHelper.MBIT;
	}
	private Action enqueueIncomingPacketAction = new Action() {
		@Override
		public void execute(Object... args){
			incomingQueue.Enqueue((MACPacket)args[0], false);
			return;
		}

	};
	

	
	

}
