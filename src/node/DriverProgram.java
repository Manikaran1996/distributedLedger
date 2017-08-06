package node;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Comparator;
import java.util.Arrays;
public class DriverProgram {
	
	public static Queue<Request> requestQueue = new LinkedList<Request>();
	private static Security security=new Security(); //added to access security features
	
	//added : nodeList to store threads
	public static NodeThread nodeList[];
	public static int n; // number of nodes
	//main DHT function that sets the previous and next pointers of nodes
	public static void buildDHT() {
		Integer tempList[]=new Integer[n];
		for(int i=0;i<n;i++) tempList[i]=i;
		Arrays.sort(tempList,new Comparator<Integer>() {
			public int compare(Integer i,Integer j) {
				BigInteger bi=new BigInteger(security.bytesToString(security.getHash(nodeList[i].getName())),16);
				BigInteger bj=new BigInteger(security.bytesToString(security.getHash(nodeList[j].getName())),16);
				return bi.compareTo(bj);
			}
		});
		nodeList[tempList[0]].DHTprev=tempList[n-1];
		nodeList[tempList[0]].DHTnext=tempList[1];
		nodeList[tempList[n-1]].DHTprev=tempList[n-2];
		nodeList[tempList[n-1]].DHTnext=tempList[0];
		for(int i=1;i<n-1;i++) {
			nodeList[tempList[i]].DHTprev=tempList[i-1];
			nodeList[tempList[i]].DHTnext=tempList[i+1];
		}
		for(int i=0;i<n;i++) nodeList[i].DHTcurr=i;
		
	}
	
	public static void main(String[] args) {

		new DriverProgram().createARequest();
	}
	
	public void createARequest() {

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
	}
	
}
