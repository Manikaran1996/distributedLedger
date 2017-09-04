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
				//System.out.println("hello");
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
			ArrayList<Output> out = temp.getValue();
			for(Output o: out) {
				if(Arrays.equals(senderPubKeyHash, o.getHash())) {
					Input in = new Input();
					in.setIndex(o.getIndex());
					in.setPrevTransaction(Long.parseLong(temp.getKey()));
					if(priK != null)
						in.createScriptSig(transactionMessage, pubKey, priK);
					inputs.add(in);
					amount += o.getValue();
					if(amount >= val) {
						return new Entries(inputs,amount);
					}
				
				}
			}
		}
		return new Entries(inputs,amount);
	}
	static public double getBalance(String pubK) {
		double amount = 0;
		byte[] senderPubKeyHash = new Security().getHash(pubK);
		Set<Map.Entry<String,ArrayList<Output>> > set = outputList.entrySet();
		Iterator<Map.Entry<String,ArrayList<Output>> > iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, ArrayList<Output>> temp = iterator.next();
			ArrayList<Output> out = temp.getValue();
			for(Output o: out) {
				if(Arrays.equals(senderPubKeyHash, o.getHash())) {
					amount += o.getValue();
				}
			}
			/*int index = getIndex(senderPubKeyHash, temp.getValue());
			if(index != -1) {
				//Output out = temp.getValue().get(index);
				Output out = null;
				for(Output o : temp.getValue()) {
					if(o.getIndex() == index && Arrays.equals(senderPubKeyHash, o.getHash())) {
						out = o;
						//break;
						amount += out.getValue();
					}
				}
				amount += out.getValue();
			}*/
		}
		return amount;
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
		ArrayList<Output> outList = outputList.get(txnId);
		for(Output o:outList) {
			if(o.getIndex() == ind)
				return o;
		}
		return null;
	}
}
