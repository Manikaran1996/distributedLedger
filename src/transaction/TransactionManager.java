package transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import node.Security;

public class TransactionManager {
	public static ArrayList<Transaction> transactionList;
	public static long maxTxnId=0; //used by broadcast mechanism
	public TransactionManager() {	
	}
	
	public static void initialize() {
		transactionList = new ArrayList<Transaction>();
		UTXO.initialize();
	}
	public static void addTransaction(Transaction t) {
		transactionList.add(t);
		System.out.println(t.getTransactionId());
		System.out.println("/* "+t.getSender()+" has sent "+t.getAmount()+" bitcoins to "+t.getReceiver()+"*/");
		UTXO.addToUnspentTxnList(String.valueOf(t.getTransactionId()), t.getOutputList());
		//System.out.println(t.hashCode() + " added to the list ");
		// remove from UTXO
		if(!t.isCoinbasedTxn())
						for(Input i: t.getInputList())
							UTXO.removeOutput(String.valueOf(i.getPrevTransactionId()),i.getIndex());
		//System.out.println("\n" + t +"\n");
		System.out.println(UTXO.outputList);
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
		t.setAmount(val);
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
		return t;
	}
	//copyList is used to copy transaction list when nodes are already running
	public static void copyList(List<Transaction> initialList) {
		Iterator<Transaction> it = initialList.iterator();
		transactionList.clear(); //delete previous entries
		while(it.hasNext()) {
			addTransaction(it.next());	
		} if(transactionList.size()!=0) maxTxnId=transactionList.get(transactionList.size()-1).getTransactionId(); 
		}
		
	
	
	public static boolean verifyTransaction(Transaction t) {
		if(t.isCoinbasedTxn())
			return true;
		ArrayList<Output> outputList = t.getOutputList();
		ArrayList<Input> inputList = t.getInputList();
		int numOfInputs = t.getInputCounter();
		int numOfOutputs = t.getOutputCounter();
		double outSum = 0, inSum = 0;
		// Check if input transactions are there in UTXO
		boolean unspent = true;
		for(int i=0;i<numOfInputs;i++) {
			Input in = inputList.get(i);
			if(!UTXO.checkIfUnspent(String.valueOf(in.getPrevTransactionId()), in.getIndex())) {
				System.out.println("Invalid 1");
				unspent = false;
				break;
			}
			inSum += UTXO.getAmount(String.valueOf(in.getPrevTransactionId()), in.getIndex());
		}
		if(!unspent)
			return false;
		//If sum of output amount is less than the input then "False Transaction"
		for(int i=0;i<numOfOutputs;i++) {
			outSum += outputList.get(i).getValue();
		}
		if(outSum != inSum) {
			
			System.out.println("Invalid 2");	
			return false;
			}
		Security s = new Security();
		for(Input in : inputList) {
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
			
		}
		return true;
	}

	public static int getHashCode() {
		List<String> list=new LinkedList<String>();
		for (Iterator iterator= transactionList.iterator(); iterator.hasNext();) {
			Transaction t = (Transaction) iterator.next();
			list.add(t.toString());
		}
		return list.hashCode();
		
	}
}
