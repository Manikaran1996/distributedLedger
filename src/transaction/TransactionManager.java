package transaction;

import java.util.ArrayList;

import node.Security;

public class TransactionManager {
	static ArrayList<Transaction> transactionList;
	public TransactionManager() {	
	}
	
	public static void addTransaction(Transaction t) {
		transactionList.add(t);
		UTXO.addToUnspentTxnList(String.valueOf(t.getTransactionId()), t.getOutputList());
		// remove from UTXO
	}
	
	
	public static Transaction createTransaction(int id, long txnId,  String sen, String rec, 
			String wit,	String senderPubKey, String rcvrKey, double val) {
		Transaction t = new Transaction();
		Security sec = new Security();
		byte[] senderPubKeyHash = sec.getHash(senderPubKey);
		//Creating input arrayList
		UTXO.Entries entry = UTXO.getInputList(senderPubKeyHash, val);
		double sumAmount = entry.getAmount();
		ArrayList<Input> in = entry.getInputList();
		if(sumAmount < val) {
			return null;
		}
		t.setInputValues(in);
		//Creating output arrayList
		Output out = new Output();
		out.setValue(val);
		out.setHash(sec.getHash(rcvrKey));
		out.setIndex(0);
		ArrayList<Output> outputs = new ArrayList<Output>();
		outputs.add(out);
		if(sumAmount > val) {
			Output changeOutput = new Output();
			changeOutput.setHash(senderPubKeyHash);
			changeOutput.setIndex(1);
			changeOutput.setValue(sumAmount-val);
			outputs.add(changeOutput);
		}
		t.setOutputValues(outputs);
		//Setting other parameters of the transaction
		t.setSender(sen);
		t.setWitness(wit);
		t.setReceiver(rec);
		t.setNodeId(id);
		t.setTransactionId(txnId);
		return t;
	}
	
	public static boolean verifyTransaction(Transaction t) {
		if(t.isCoinbasedTxn())
			return true;	// Cryptographic verification on output is to be done
		ArrayList<Output> outputList = t.getOutputList();
		ArrayList<Input> inputList = t.getInputList();
		int numOfInputs = t.getInputCounter();
		int numOfOutputs = t.getOutputCounter();
		double outSum = 0, inSum = 0;
		// Check if input transactions are there in UTXO
		boolean unspent = true;
		for(int i=0;i<numOfInputs;i++) {
			Input in = inputList.get(i);
			if(!UTXO.checkIfUnspent(String.valueOf(in.getPrevTransactionNum()), in.getIndex())) {
				unspent = false;
				break;
			}
			inSum += UTXO.getAmount(String.valueOf(in.getPrevTransactionNum()), in.getIndex());
		}
		if(!unspent)
			return false;
		//If sum of output amount is less than the input then "False Transaction"
		for(int i=0;i<numOfOutputs;i++) {
			outSum += outputList.get(i).getValue();
		}
		if(outSum > inSum)
			return false;
		else
			return true; // until cryptographic verification is to be implemented
		//TODO Cryptographic verification
		
		//return false;
	}

	public static int getHashCode() {
		return transactionList.hashCode();
		
	}
}
