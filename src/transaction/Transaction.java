package transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import node.Security;

public class Transaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3959822816295112067L;
	private long txnId;
	private String receiver, sender, witness;
	private double amount;
	private ArrayList<Input> inputList;
	private int inCounter, outCounter;
	private ArrayList<Output> outputList;
	private int nodeId;
	private boolean coinBase;
	byte[] digitalSignature;
	
	public Transaction() {
		coinBase = false;
		digitalSignature = null;
	}
	
	public void setSender(String sen) {
		sender = sen;
	}
	
	public void setReceiver(String rcvr) {
		receiver = rcvr;
	}
	
	public void setWitness(String wit) {
		witness = wit;
	}
	
	public void setTransactionId(long txnId) {
		this.txnId = txnId;
	}
	
	public void setInputValues(ArrayList<Input> inputList) {
		this.inputList = inputList;
		inCounter = inputList.size();
	}
	
	public void setOutputValues(ArrayList<Output> outList) {
		this.outputList = outList;
		outCounter = outList.size();
	}
	
	public void setNodeId(int id) {
		nodeId = id;
	}
	
	public int getInputCounter() {
		return inCounter;
	}

	public int getOutputCounter() {
		return outCounter;
	}
	
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
	
	public ArrayList<Input> getInputList() {
		return inputList;
	}
	
	public ArrayList<Output> getOutputList() {
		return outputList;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public boolean isCoinbasedTxn() {
		return coinBase;
	}
	
	public String getTransactionAsMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(txnId);
		builder.append(nodeId);
		builder.append(sender);
		builder.append(receiver);
		builder.append(witness);
		//builder.append(inCounter);
		/*for(Input in : inputList)
			builder.append(in.toString());
		builder.append(outCounter);
		for(Output out : outputList)
			builder.append(out); */
		return builder.toString();
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
		if(receiverPublicKey != null)
			out.setHash(new Security().getHash(receiverPublicKey));
		out.setValue(bitcoins);
		txn.outputList = new ArrayList<Output>();
		txn.outputList.add(out);
		txn.coinBase = true;
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
	
	@Override
	public int hashCode() {
		return Objects.hash(txnId, receiver, sender, witness, amount, inputList, outputList, nodeId);
	}

}
