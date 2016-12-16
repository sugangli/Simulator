package edu.rutgers.winlab.sim.core;

import java.util.LinkedList;

public class FIFOQueue<T> extends SimulatorQueue<T> {
	
	public String Name;
	public int Capacity;
	public int Size;
	
	private LinkedList<T> innerQueuePrioritized = new LinkedList<T>();
	private LinkedList<T> innerQueue = new LinkedList<T>();
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getCapacity() {
		return Capacity;
	}
	public void setCapacity(int capacity) {
		Capacity = capacity;
	}
	public int getSize() {
		return innerQueuePrioritized.size() + innerQueue.size();
	}
	public void setSize(int size) {
		Size = size;
	}
	
	public FIFOQueue(int capacity){
		Capacity = capacity;
	}
	
	public void AddData(T item, boolean prioritized){
		
		if(prioritized)
			innerQueuePrioritized.addLast(item);
		else
			innerQueue.addLast(item);
		
		while(getSize() > Capacity && innerQueue.size() > 0){
			T node = innerQueue.getLast();
			System.out.printf("Drop Packet: %s", node.toString());
			innerQueue.removeLast();
		}
		
		while(getSize() > Capacity){
			T node = innerQueuePrioritized.getLast();
			System.out.printf("Drop Packet: %s", node.toString());
			innerQueuePrioritized.removeLast();
		}
	}
	
	public T GetData(){
		if(innerQueuePrioritized.size() > 0){
			T node = innerQueuePrioritized.getFirst();
			innerQueuePrioritized.removeFirst();
			return node;
		}else{
			T node = innerQueue.getFirst();
			innerQueue.removeFirst();
			return node;
		}
	}
	

}
