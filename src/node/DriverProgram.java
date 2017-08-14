package node;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

import node.Request.RequestCodes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
public class DriverProgram {
	
	private static Security security=new Security(); //added to access security features
	static HashMap<String, NodeThread> hashMap = new HashMap<String, NodeThread>();
	//main DHT function that sets the previous and next pointers of nodes
	public static void buildDHT() {
		int n=hashMap.size();
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
		NodeThread sender = new NodeThread("sender", hashMap);
		NodeThread receiver = new NodeThread("receiver", hashMap);
		NodeThread witness = new NodeThread("witness", hashMap);
		hashMap.put("sender", sender);
		hashMap.put("receiver", receiver);
		hashMap.put("witness",witness);
		buildDHT();
		sender.putDHTValue("sender", sender.pubKey);
		receiver.putDHTValue("receiver", receiver.pubKey);
		witness.putDHTValue("witness", witness.pubKey);
		
		sender.start();
		receiver.start();
		witness.start();
		Request request = new Request();
		request.setRequestCode(RequestCodes.COMMIT);
		sender.putRequest(request);
		sender.getDHTValue("receiver");
		receiver.getDHTValue("witness");
		witness.getDHTValue("sender");
		
		//sender.commitTransaction();
		new DriverProgram();
	}
	
	
	/* public void createARequest() {

		n=4;
		nodeList=new NodeThread[n];
		for(int i=0;i<n;i++)
			nodeList[i]=new NodeThread("Node"+Integer.toString(i+1),nodeList);
		buildDHT();		
		Request myRequest = new Request();
		myRequest.genericRequest();
		requestQueue.add(myRequest);
		try {
			Thread.currentThread().sleep(500); //added otherwise Main thread was notifying too early before other threads called wait()
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		synchronized(requestQueue) {
			requestQueue.notifyAll();
		}
	} */
	
}
