package transaction;

import node.Security;

public class Output {
	double value;
	byte[] hashOfReceiverKey;
	
	public double getValue() {
		return value;
	}
	
	public byte[] getHash() {
		return hashOfReceiverKey;
	}
	
	public void setValue(double val) {
		value = val;
	}
	
	public void setHash(byte[] hash) {
		hashOfReceiverKey = hash;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Value : " + value);
		builder.append("\nHash : " + Security.bytesToString(hashOfReceiverKey));
		return builder.toString();
	}
}
