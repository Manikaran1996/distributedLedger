package transaction;

import java.security.PublicKey;

public class Input {
	
	private long prevTransaction;
	private int index;
	private PublicKey pubKey;
	private byte[] signature;

	void setPrevTransaction(long tx) {
		prevTransaction = tx;
	}
	
	void setIndex(int ind) {
		index = ind;
	}
	
	void setScriptSig(byte[] sig, PublicKey pk) {
		signature = sig;
		pubKey = pk;
	}
	
	long getPrevTransactionNum() {
		return prevTransaction;
	}
	
	int getIndex() {
		return index;
	}
	
	PublicKey getPublicKey() {
		return pubKey;
	}
	
	byte[] getSignature() {
		return signature;
	}
}
