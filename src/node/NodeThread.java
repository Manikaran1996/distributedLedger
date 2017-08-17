package node;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import node.Request.RequestCodes;
import node.TwoPhaseProtocol.MessageCodes;
import transaction.Transaction;

public class NodeThread extends Thread {
	
	private BlockingQueue<Request> mailBox;
	int DHTprev=-1;
	int DHTnext=-1;
	int DHTcurr=-1;
	String pubKey,priKey; //added data member to store keys
	Security security; //to use hash/key generation
	HashMap<String,String> DHTable;
	HashMap<String, NodeThread> threadMap;
	private CommitThread commitThread;
	//DHT functions
	private static final BigInteger DHT_MAX=new BigInteger("10000000000000000000000000000000000000000",16); //=2**160
	public NodeThread(String threadName, HashMap<String, NodeThread> map) {
		super(threadName);
		mailBox = new LinkedBlockingQueue<Request>();
		DHTable = new HashMap<String,String>();
		threadMap = map;
		commitThread = null;
		start();
	}
	

	
	public void run() {
		/*try {
			security=new Security();
			pubKey=security.getPublicKey();
			priKey=security.getPrivateKey();
			synchronized(DriverProgram.requestQueue) {
				DriverProgram.requestQueue.wait();
				putDHTValue(getName(),pubKey); // using thread name itself as key hence will map to itselfs
				putDHTValue("Node"+(DHTcurr+4),"dummy"); //dummy to show storage of extra nodes for testing 
				Request r = DriverProgram.requestQueue.peek();
				if(r.getRequestCode() == RequestCodes.DEFAULT) {
					System.out.println("Hello I am a thread and my name is " + getName());
				}
			getDHTValue(getName());
			getDHTValue("Node"+(DHTcurr+4));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		do {
			Request request;
			try {
				request = mailBox.take();
				if(request.getRequestCode() == RequestCodes.COMMIT) {
					commitTransaction("receiver", "witness");
				}
				else if(request.getRecipient() == getName()) {
					if(request.getRequestCode() == RequestCodes.TWO_PHASE) {
						if(request.getMessage().equals(String.valueOf(MessageCodes.PREPARE))) {
							Request readyReply = TwoPhaseProtocol.getReadyMessage(getName(), request.getSender());
							NodeThread t = threadMap.get(request.getSender());
							t.putRequest(readyReply);
						}
						else if(request.getMessage().equals(String.valueOf(MessageCodes.COMMIT))) {
							Request commitReply = TwoPhaseProtocol.getCommitAckMessage(getName(), request.getSender());
							NodeThread t = threadMap.get(request.getSender());
							t.putRequest(commitReply);
						}
					}
					else if(request.getRequestCode() == RequestCodes.TWO_PHASE_REPLY) {
						if(commitThread != null) {
							commitThread.putRequest(request);
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} while(true);
	}
	
	
	public void putRequest(Request req) {
		try {
			mailBox.put(req);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* private BigInteger DHTDistance(String i,String j) {
		BigInteger bi=new BigInteger(i,16);
		BigInteger bj=new BigInteger(j,16);
		switch(bi.compareTo(bj)) {
		case 0: return BigInteger.ZERO;
		case -1: return bj.subtract(bi);
		case 1: bj.subtract(bi);
				return bj.add(DHT_MAX);
			}
		return BigInteger.ZERO;
		}
	private int DHTFindNode(String keyVal) {
		int currNode=DHTcurr;
		String keyValHash=security.bytesToString(security.getHash(keyVal));
		BigInteger a=DHTDistance(security.bytesToString(security.getHash(nList[currNode].getName())),keyValHash);
		BigInteger b=DHTDistance(security.bytesToString(security.getHash(nList[nList[currNode].DHTnext].getName())),keyValHash);
		while(a.compareTo(b)==1) {
			currNode=DriverProgram.nodeList[currNode].DHTnext;
			a=DHTDistance(security.bytesToString(security.getHash(nList[currNode].getName())),keyValHash);
			b=DHTDistance(security.bytesToString(security.getHash(nList[nList[currNode].DHTnext].getName())),keyValHash);	
		}
		return currNode;
		}
	synchronized public String getDHTValue(String keyVal) {
		int reqNode=DHTFindNode(keyVal);
		String temp= DriverProgram.nodeList[reqNode].DHTable.get(keyVal);
		System.out.println("Node "+(reqNode+1) + "returns value of "+keyVal+ " as "+temp);
		return temp;
		}
	synchronized public void putDHTValue(String keyVal,String Value) {
		int reqNode=DHTFindNode(keyVal);
		DriverProgram.nodeList[reqNode].DHTable.put(keyVal,Value);
		System.out.println("Node "+(reqNode+1) + "stored value of "+keyVal+ " as "+Value);
		} */
	
	public String query(String nodeName) {
		return null;
	}
	
	public String verify(Transaction t) {
		return null;
	}
	
	private void commitTransaction(String rec, String wit) { 
		commitThread = new CommitThread(getName(), rec, wit, this, threadMap.get(rec), threadMap.get(wit));
		
	}

}
