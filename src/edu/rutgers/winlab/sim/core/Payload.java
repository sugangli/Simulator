package edu.rutgers.winlab.sim.core;

public class Payload implements ISerializable{

	private int SizeInBits;
	private String Val;
	
	public Payload(String val){
		Val = val;
	}

	public int getSizeInBits() {
		return SizeInBits;
	}

	public void setSizeInBits(int sizeInBits) {
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