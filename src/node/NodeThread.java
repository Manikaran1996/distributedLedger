package node;
import java.util.HashMap;
public class NodeThread extends Thread {
	int DHTprev=-1;
	int DHTnext=-1;
	int DHTcurr=-1;
	HashMap<Integer,Integer> DHTable=new HashMap<Integer,Integer>();
	//DHT functions
	private static final int DHT_MAX=8; //2**k if k bit hash used
	private int DHTDistance(int i,int j) {
		if (i==j) return 0;
		else if (i<j) return j-i;
		else return j-i+DHT_MAX;
		}
	private int DHTFindNode(int keyVal) {
		int currNode=DHTcurr;
		int keyValHash=DriverProgram.mhash(keyVal);
		while( DHTDistance(DriverProgram.mhash(currNode),keyValHash) >DHTDistance(DriverProgram.mhash(DriverProgram.nodeList[currNode].DHTnext),keyValHash))
			currNode=DriverProgram.nodeList[currNode].DHTnext;
		return currNode;
		}
	synchronized public int getDHTValue(int keyVal) {
		int reqNode=DHTFindNode(keyVal);
		int temp= DriverProgram.nodeList[reqNode].DHTable.get(keyVal);
		System.out.println("Node "+(reqNode+1) + "returns value of "+keyVal+ " as "+temp);
		return temp;
		}
	synchronized public void putDHTValue(int keyVal,int Value) {
		int reqNode=DHTFindNode(keyVal);
		DriverProgram.nodeList[reqNode].DHTable.put(keyVal,Value);
		System.out.println("Node "+(reqNode+1) + "stored value of "+keyVal+ " as "+Value);
		}
	public NodeThread(String threadName) {
		super(threadName);
		start();
	}
	
	public void run() {
		try {
			synchronized(DriverProgram.requestQueue) {
				DriverProgram.requestQueue.wait();
				putDHTValue(8-DHTcurr,8-DHTcurr);
				sleep(500);
				Request r = DriverProgram.requestQueue.peek();
				if(r.getRequestCode() == -1) {
					System.out.println("Hello I am a thread and my name is " + getName());
				}
			getDHTValue(8-DHTcurr);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String query(String nodeName) {
		return null;
	}
	
	public String verify(Transaction t) {
		return null;
	}
	

}
