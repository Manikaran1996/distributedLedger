package transaction;

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
}
