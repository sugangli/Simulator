package edu.rutgers.winlab.sim.core;

public class Payload implements ISerializable{

	private long SizeInBits;
	private String Val;
	
	public Payload(String val){
		Val = val;
	}

	public long getSizeInBits() {
		return SizeInBits;
	}

	public void setSizeInBits(long sizeInBits) {
		SizeInBits = sizeInBits;
	}
	
	public String getVal() {
		return Val;
	}

	public void setVal(String val) {
		Val = val;
	}

	public String toString(){
		return String.format("Payload:{%s}", getVal());
	}
	

}