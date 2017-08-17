package node;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

import node.Request.RequestCodes;
import transaction.Transaction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
public class DriverProgram {
	
	private Security security=new Security(); //added to access security features
	private HashMap<String, NodeThread> hashMap = new HashMap<String, NodeThread>();
	private LinkedList<Transaction> txnList;
	
	public DriverProgram() {
		txnList = new LinkedList<Transaction>();
	}
	
	//main DHT function that sets the previous and next pointers of nodes
	public void buildDHT() {
		int n= hashMap.size();
		Set<String> namesSet=hashMap.keySet(); 
		String tempList[]=new String[n];
		int ind=0;
		for (Iterator<String> iterator = namesSet.iterator(); iterator.hasNext();) {
			tempList[ind++]=(String) iterator.next();
			
		}
		Arrays.sort(tempList,new Comparator<String>() {
			public int compare(String i,String j) {
				BigInteger bi=new BigInteger(security.bytesToString(security.getHash(i)),16);
				BigInteger bj=new BigInteger(security.bytesToString(security.getHash(j)),16);
				return bi.compareTo(bj);
			}
		});
		hashMap.get(tempList[0]).DHTprev=tempList[n-1];
		hashMap.get(tempList[0]).DHTnext=tempList[1];
		hashMap.get(tempList[n-1]).DHTprev=tempList[n-2];
		hashMap.get(tempList[n-1]).DHTnext=tempList[0];
		for(int i=1;i<n-1;i++) {
			hashMap.get(tempList[i]).DHTprev=tempList[i-1];
			hashMap.get(tempList[i]).DHTnext=tempList[i+1];
		}
	} 
	
	public static void main(String[] args) {
		/* NodeThread sender = new NodeThread("sender", hashMap);
		NodeThread receiver = new NodeThread("receiver", hashMap);
		NodeThread witness = new NodeThread("witness", hashMap);
		hashMap.put("sender", sender);
		hashMap.put("receiver", receiver);
		hashMap.put("witness",witness); */
		
		// instead of making every function static I have made an object of the class
		// and called the functions on the object
		
		DriverProgram dp = new DriverProgram();
		
		for(int i=1;i<=5;i++) {
			dp.hashMap.put(String.valueOf(i), new NodeThread(String.valueOf(i), dp.hashMap));
		}
		dp.initialize();
		for(int i=1;i<=5;i++) {
			NodeThread temp = dp.hashMap.get(String.valueOf(i));
			temp.copyList(dp.txnList);
			temp.start();
		}
		
		dp.buildDHT();
		
		//sender.putDHTValue("sender", sender.pubKey);
		//receiver.putDHTValue("receiver", receiver.pubKey);
		//witness.putDHTValue("witness", witness.pubKey);
		/* witness.putDHTValue("demo","demo");
		sender.start();
		receiver.start();
		witness.start();
		Request request = new Request();
		request.setRequestCode(RequestCodes.COMMIT);
		sender.putRequest(request);
		Thread t=Thread.currentThread();
		synchronized (t) {
			try {
				t.wait(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		sender.getDHTValue("receiver");
		receiver.getDHTValue("witness");
		witness.getDHTValue("sender");
		witness.getDHTValue("demo");
		witness.getDHTValue("demo");
		//sender.commitTransaction(); */
	}

	private void initialize() {
		int mapSize = hashMap.size();
		long txId = 1;
		for(int i=1;i<=mapSize;i++) {
			txnList.add(Transaction.getDummyTransaction(hashMap.get(String.valueOf(i)).pubKey, 100.0, txId , String.valueOf(i)));
			txId++;
			
		}
	}
	
}
