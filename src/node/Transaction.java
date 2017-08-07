package node;

import java.util.LinkedList;

public class Transaction {
	private long txnId;
	private String digitalSignature, receiver, sender, witness;
	private double amount;
	private LinkedList<Transaction> input;
	
	public long getTransactionId() {
		return txnId;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getWitness() {
		return witness;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public String getDigitalSignature() {
		return digitalSignature;
	}
	
	public LinkedList<Transaction> getInputList() {
		return input;
	}
	
	public void createTransaction(long id, String rec, String sen, String wit, Double amt, LinkedList<Transaction> input, String sig) {
		txnId = id;
		receiver = rec;
		sender = sen;
		amount = amt;
		this.input = input;
		digitalSignature = sig;
		
	}
}
