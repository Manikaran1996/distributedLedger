package transaction;

import java.util.ArrayList;

import node.Security;

public class Transaction {
	private long txnId;
	private String digitalSignature, receiver, sender, witness;
	private double amount;
	private ArrayList<Input> inputList;
	private int inCounter, outCounter;
	private ArrayList<Output> outputList;
	private int nodeId;
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
		receiver = rec;
		sender = sen;
		amount = amt;
		digitalSignature = sig;
		txnId = id; 
	}
	
	public static Transaction getDummyTransaction(String receiverPublicKey, double bitcoins, long id, String receiver) {
		Transaction txn = new Transaction();
		txn.outCounter = 1;
		txn.inCounter = 0;
		txn.inputList = null;
		txn.sender = null;
		txn.witness = null;
		txn.receiver = receiver;
		txn.txnId = id;
		txn.nodeId = 0;
		Output out = new Output();
		out.setHash(new Security().getHash(receiverPublicKey));
		out.setValue(bitcoins);
		txn.outputList = new ArrayList<Output>();
		txn.outputList.add(out);
		return txn;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionId : " + txnId);
		builder.append("\nNode Id : " + nodeId);
		builder.append("\nReceiver : " + receiver);
		builder.append("\nSender : " + sender);
		builder.append("\nWitness : " + witness);
		builder.append("\nInCounter : " + inCounter);
		builder.append("\nOutCounter : " + outCounter);
		builder.append("\nAmount : " + amount);
		builder.append("\nInput List : \n");
		if(inputList != null) {
			int inLength = inputList.size();
			for(int i=1;i<=inLength;i++) {
				builder.append("Input Number " + i + "-\n");
				builder.append(inputList.get(i-1));
			}
		}
		builder.append("\nOutput List : \n");
		int outLength = outputList.size();
		for(int i=1;i<=outLength;i++) {
			builder.append("Output Number " + i + "-\n");
			builder.append(outputList.get(i-1));
		}
		return builder.toString();
	}
}
