package transaction;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

import node.Security;

public class Input implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3312467074814794412L;
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
	
	void createScriptSig(String transactionMessage, PublicKey pubK, PrivateKey pk) {
		pubKey = pubK;
		signature = new Security().createSignature(transactionMessage, pk, pubK);
	}
	
	long getPrevTransactionId() {
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
	
	//TODO
	public String toString() {
		return "";
	}
}
