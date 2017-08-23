package transaction;

import node.Security;

public class Output {
	double value;
	byte[] hashOfReceiverKey;
	int index;
	
	public double getValue() {
		return value;
	}
	
	public byte[] getHash() {
		return hashOfReceiverKey;
	}
	
	public int getIndex() {
		return index;
	}
	public void setValue(double val) {
		value = val;
	}
	
	public void setHash(byte[] hash) {
		hashOfReceiverKey = hash;
	}
	
	public void setIndex(int ind) {
		ind = index;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Value : " + value);
		builder.append("\nHash : " + Security.bytesToString(hashOfReceiverKey));
		return builder.toString();
	}
}
