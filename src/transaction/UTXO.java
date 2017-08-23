package transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UTXO {
	static HashMap<String, ArrayList<Output> > outputList;
	
	static void addToUnspentTxnList(String txnId, ArrayList<Output> val) {
		outputList.put(txnId, val);
	}
	
	static boolean checkIfUnspent(String txnId, int index) {
		ArrayList<Output> temp = outputList.get(txnId);
		if(temp == null)
			return false;
		else {
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
			if(outputs.get(i).getHash().equals(senderPubKeyHash)) {
				return i;
			}
		}
		return -1;
	}
	
	//TODO
	static Entries getInputList(byte[] senderPubKeyHash, double val) {
		double amount = 0;
		Set<Map.Entry<String,ArrayList<Output>> > set = outputList.entrySet();
		Iterator<Map.Entry<String,ArrayList<Output>> > iterator = set.iterator();
		ArrayList<Input> inputs = new ArrayList<Input>();
		while(iterator.hasNext()) {
			Map.Entry<String, ArrayList<Output>> temp = iterator.next();
			int index = getIndex(senderPubKeyHash, temp.getValue());
			if(index != -1) {
				Output out = temp.getValue().get(index);
				amount += out.getValue();
				Input in = new Input();
				in.setIndex(index);
				in.setPrevTransaction(Long.parseLong(temp.getKey()));
				// TODO set public key and the signature
				in.setScriptSig(null, null);
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
}
