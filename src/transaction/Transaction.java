package transaction;

import java.util.ArrayList;

public class Transaction {
	private long txnId;
	private String digitalSignature, receiver, sender, witness;
	private double amount;
	private ArrayList<Input> inputList;
	private int inCounter, outCounter;
	private ArrayList<Output> outputList;
	
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
	
	public ArrayList<Input> getInputList() {
		return inputList;
	}
	
	public void createTransaction(long id, String rec, String sen, String wit, Double amt, String sig) {
		txnId = id;
		receiver = rec;
		sender = sen;
		amount = amt;
		digitalSignature = sig;
		
	}
}
