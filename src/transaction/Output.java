package transaction;

import java.io.Serializable;

import node.Security;

public class Output implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 487859790383385000L;
	private double value;
	private byte[] hashOfReceiverKey;
	private int index;
	
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
		index = ind;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Value : " + value);
		if(hashOfReceiverKey!=null)builder.append("\nHash : " + Security.bytesToString(hashOfReceiverKey));
		return builder.toString();
	}
}
