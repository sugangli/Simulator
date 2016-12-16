package edu.rutgers.winlab.sim.core;

public abstract class SimulatorQueue<T> {
	private String Name;
	private int Capacity;
	private int Size;
	private T item;
	
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
		return Size;
	}

	public void setSize(int size) {
		Size = size;
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	
	public void AddData(T item, boolean isPrioritized){};
	
	public T GetData(){return item;};
	
	
	
	
}
