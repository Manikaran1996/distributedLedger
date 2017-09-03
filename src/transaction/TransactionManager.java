package transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import node.Security;

public class TransactionManager {
	public static ArrayList<Transaction> transactionList;
	public TransactionManager() {	
	}
	
	public static void initialize() {
		transactionList = new ArrayList<Transaction>();
		UTXO.initialize();
	}
	public static void addTransaction(Transaction t) {
		transactionList.add(t);
		
		UTXO.addToUnspentTxnList(String.valueOf(t.getTransactionId()) + "," + t.getNodeId(), t.getOutputList());
		System.out.println(t.getTransactionId() + " added to the list ");
		if(!t.isCoinbasedTxn())
			for(Input i: t.getInputList())
				UTXO.removeOutput(i.getPrevTransactionId(),i.getIndex());
		//System.out.println("\n" + t +"\n");
	}
	
	
	public static Transaction createTransaction(int id, long txnId,  String sen, String rec, 
			String wit,	String senderPubKey, String rcvrKey, double val, PrivateKey pk) {
		Transaction t = new Transaction();
		Security sec = new Security();
		t.setSender(sen);
		t.setWitness(wit);
		t.setReceiver(rec);
		t.setNodeId(id);
		t.setTransactionId(txnId);
		byte[] senderPubKeyHash = sec.getHash(senderPubKey);
		//Creating input arrayList
		UTXO.Entries entry = UTXO.getInputList(t.getTransactionAsMessage(), senderPubKey, pk,  val);
		double sumAmount = entry.getAmount();
		ArrayList<Input> in = entry.getInputList();
		if(sumAmount < val) {
			System.out.println("You have unsufficient balance (" + sumAmount +"), So transaction can not be proceeded.");
			return null;
		}
		t.setInputValues(in);
		//Creating output arrayList
		Output out = new Output();
		out.setValue(val);
		if(rcvrKey != null)
			out.setHash(sec.getHash(rcvrKey));
		out.setIndex(0);
		ArrayList<Output> outputs = new ArrayList<Output>();
		outputs.add(out);
		if(sumAmount > val) {
			Output changeOutput = new Output();
			if(senderPubKeyHash != null)
				changeOutput.setHash(senderPubKeyHash);
			changeOutput.setIndex(1);
			changeOutput.setValue(sumAmount-val);
			outputs.add(changeOutput);
		}
		t.setOutputValues(outputs);
		System.out.println(t);
		return t;
	}
	
	public static boolean verifyTransaction(Transaction t) {
		if(t.isCoinbasedTxn())
			return true;
		ArrayList<Output> outputList = t.getOutputList();
		ArrayList<Input> inputList = t.getInputList();
		int numOfInputs = t.getInputCounter();
		int numOfOutputs = t.getOutputCounter();
		double outSum = 0, inSum = 0;
		System.out.println("Input : ");
		System.out.println(inputList);
		// Check if input transactions are there in UTXO
		boolean unspent = true;
		for(int i=0;i<numOfInputs;i++) {
			Input in = inputList.get(i);
			System.out.print(in.getPrevTransactionId());
			if(!UTXO.checkIfUnspent(String.valueOf(in.getPrevTransactionId()), in.getIndex())) {
				unspent = false;
				break;
			}
			inSum += UTXO.getAmount(String.valueOf(in.getPrevTransactionId()), in.getIndex());
		}
		System.out.println("unspent : " + unspent);

		if(!unspent) {
			System.out.println(UTXO.outputList);
			return false;
		}
		//If sum of output amount is less than the input then "False Transaction"
		for(int i=0;i<numOfOutputs;i++) {
			outSum += outputList.get(i).getValue();
		}
		if(outSum != inSum)
			return false;
		Security s = new Security();
		/*for(Input in : inputList) {
			Output out = UTXO.getOutputOf(String.valueOf(in.getPrevTransactionId()), in.getIndex());
			byte[] prevOutputRecHash = out.getHash();
			PublicKey pubKey = in.getPublicKey();
			byte[] sig = in.getSignature();
			byte[] currentInPubKeyHash = s.getHash(Security.bytesToString(pubKey.getEncoded()));
			if(Arrays.equals(prevOutputRecHash, currentInPubKeyHash)) {
				if(s.verifySignature(t.getTransactionAsMessage(), sig, pubKey)) {
					return true;
				}
				else break;
			}
			else
				break;
			
		}*/
		return true;
	}

	public static int getHashCode() {
		return transactionList.hashCode();
		
	}
}
