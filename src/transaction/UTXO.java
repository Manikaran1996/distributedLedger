package transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import node.Security;

public class UTXO {
	static HashMap<String, ArrayList<Output> > outputList;
	
	static void initialize() {
		outputList = new HashMap<String, ArrayList<Output>>();
	}
	
	static void addToUnspentTxnList(String txnId, ArrayList<Output> val) {
		if(outputList != null)
			outputList.put(txnId, val);
		else initialize();
	}
	
	static boolean checkIfUnspent(String txnId, int index) {
		ArrayList<Output> temp = outputList.get(txnId);
		if(temp == null)
			return false;
		else {
			System.out.println(temp);
			int size = temp.size();
			for(int i=0;i<size;i++) {
				if(temp.get(i).getIndex() == index)
					return true;
			}
		}
		return false;
	}
	
	static boolean removeOutput(String txnId, int index) {
		ArrayList<Output> temp = outputList.get(txnId);
		if(temp == null)
			return false;
		else {
			int size = temp.size();
			for(int i=0;i<size;i++) {
				if(temp.get(i).getIndex() == index) {
					temp.remove(i);
					if(temp.size() > 0) {
						outputList.put(txnId, temp);
					}
					else {
						outputList.remove(txnId);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private static int getIndex(byte[] senderPubKeyHash, ArrayList<Output> outputs) {
		int size = outputs.size();
		for(int i=0;i<size;i++) {
			if(Arrays.equals(outputs.get(i).getHash(), senderPubKeyHash)) {
				return outputs.get(i).getIndex();
			}
		}
		return -1;
	}
	
	//TODO
	static Entries getInputList(String transactionMessage, String pubK, PrivateKey priK, double val) {
		double amount = 0;
		byte[] senderPubKeyHash = new Security().getHash(pubK);
		PublicKey pubKey = Security.getPublicKeyFromString(pubK);
		Set<Map.Entry<String,ArrayList<Output>> > set = outputList.entrySet();
		Iterator<Map.Entry<String,ArrayList<Output>> > iterator = set.iterator();
		ArrayList<Input> inputs = new ArrayList<Input>();
		while(iterator.hasNext()) {
			Map.Entry<String, ArrayList<Output>> temp = iterator.next();
			int index = getIndex(senderPubKeyHash, temp.getValue());
			if(index != -1) {
				Output out = null;
				for(Output o : temp.getValue()) {
					if(o.getIndex() == index) {
						out = o;
						break;
					}
				}
				amount += out.getValue();
				Input in = new Input();
				in.setIndex(index);
				in.setPrevTransaction(temp.getKey());
				if(priK != null)
					in.createScriptSig(transactionMessage, pubKey, priK);
				inputs.add(in);
				if(amount >= val)
					break;
			}
		}
		return new Entries(inputs,amount);
	}
	
	static class Entries {
		ArrayList<Input> inputs;
		double amount;
		Entries(ArrayList<Input> in, double amt) {
			inputs = in;
			amount = amt;
		}
		
		double getAmount() {
			return amount;
		}
		
		ArrayList<Input> getInputList() {
			return inputs;
		}
	}

	public static double getAmount(String txnId, int index) {
		ArrayList<Output> outputs = outputList.get(txnId);
		if(outputs == null) {
			return Double.MIN_VALUE;
		}
		int numOfOutputs = outputs.size();
		for(int i=0;i<numOfOutputs;i++) {
			if(outputs.get(i).getIndex() == index)
				return outputs.get(i).getValue();
		}
		return 0;
	}
	
	public static Output getOutputOf(String txnId, int ind) {
		return outputList.get(txnId).get(ind);
	}
}
