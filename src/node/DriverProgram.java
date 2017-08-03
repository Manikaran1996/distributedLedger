package node;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Comparator;
import java.util.Arrays;
public class DriverProgram {
	
	public static Queue<Request> requestQueue = new LinkedList<Request>();
	
	//added : nodeList to store threads
	public static NodeThread nodeList[];
	public static int n; // number of nodes
	//temp function that acts as hash
	public static int mhash(int i) {
		return (i)%n;
	}
	//main DHT function that sets the previous and next pointers of nodes
	public static void buildDHT() {
		Integer tempList[]=new Integer[n];
		for(int i=0;i<n;i++) tempList[i]=i;
		Arrays.sort(tempList,new Comparator<Integer>() {
			public int compare(Integer i,Integer j) {
				return mhash(i)-mhash(j);
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
			nodeList[i]=new NodeThread("Node"+Integer.toString(i+1));
		buildDHT();		
		for(int i=0;i<4;i++) 
			System.out.println("node "+i+" "+nodeList[i].DHTprev+" "+nodeList[i].DHTnext);		
		Request myRequest = new Request();
		myRequest.genericRequest();
		requestQueue.add(myRequest);
		
		synchronized(requestQueue) {
			requestQueue.notifyAll();
		}
	}
	
}
