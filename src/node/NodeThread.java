package node;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import node.Request.RequestCodes;
import node.TwoPhaseProtocol.MessageCodes;

public class NodeThread extends Thread {
	int DHTprev=-1;
	int DHTnext=-1;
	int DHTcurr=-1;
	String pubKey,priKey; //added data member to store keys
	Security security; //to use hash/key generation
	HashMap<String,String> DHTable=new HashMap<String,String>();
	NodeThread nList[];//rather than referencing DriverProgram for list of ThreadNode, kept a reference through constructor
	//DHT functions
	private static final BigInteger DHT_MAX=new BigInteger("10000000000000000000000000000000000000000",16); //=2**160
	private BigInteger DHTDistance(String i,String j) {
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
		}
	public NodeThread(String threadName,NodeThread nt[]) {
		super(threadName);
		nList=nt;
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
		while(true) {
			synchronized(this) {
				try {
					wait();
					System.out.println(getName() + " awake ");
					Iterator<Request> iterator = DriverProgram.requestQueue.iterator();
					while(iterator.hasNext()) {
						Request r = iterator.next();
						if(r.getRecipient() == getName()) {
							iterator.remove();
							if(r.getRequestCode() == RequestCodes.TWO_PHASE) {
								if(r.getMessage().equals(String.valueOf(MessageCodes.PREPARE))) {
									Request readyReply = TwoPhaseProtocol.getReadyMessage(getName(), "sender");
									DriverProgram.requestQueue.add(readyReply);
								}
							}
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String query(String nodeName) {
		return null;
	}
	
	public String verify(Transaction t) {
		return null;
	}
	
	public synchronized boolean commitTransaction() {
		// Start Two phase protocol
		Request prepareMessageReceiver = TwoPhaseProtocol.getPrepareMessage(getName(), "receiver"); // hard-coding for now, will be using DHT.
		Request prepareMessageWitness = TwoPhaseProtocol.getPrepareMessage(getName(), "witness"); // hard-coding for now, will be using DHT.
		DriverProgram.requestQueue.add(prepareMessageReceiver);
		DriverProgram.requestQueue.add(prepareMessageWitness);
		try {
			notifyAll();
			wait(2000);
			Iterator<Request> iterator = DriverProgram.requestQueue.iterator();
			boolean receiverReady = false, witnessReady = false;
			while(iterator.hasNext() && (!receiverReady || !witnessReady)) {
				Request temp = iterator.next();
				if(temp.getRecipient() == getName() && temp.getRequestCode() == RequestCodes.TWO_PHASE) {
					String messageFrom = temp.getSender();
					if(messageFrom.equals("receiver")) {
						iterator.remove();
						if(temp.getMessage().equals(String.valueOf(MessageCodes.READY)))
							receiverReady = true;
						else
							break;
					}
					else if(messageFrom.equals("witness")) {
						if(temp.getMessage().equals(String.valueOf(MessageCodes.READY)))
							witnessReady = true;
						else
							break;
					}
				}
				
			}

			System.out.println(DriverProgram.requestQueue.peek());
			if(!receiverReady || !witnessReady) {	// in case of time out or if any of the receiver or witness replies with NOT_READY
				Request abortMessageReceiver = TwoPhaseProtocol.getAbortMessage(getName(), "receiver");
				Request abortMessageWitness = TwoPhaseProtocol.getAbortMessage(getName(), "witness");
				DriverProgram.requestQueue.add(abortMessageWitness);
				DriverProgram.requestQueue.add(abortMessageReceiver);
				// TODO - complete
			}
			else {
				System.out.println("Got Ready Message from the sender and the witness");
				Request commitMessageReceiver = TwoPhaseProtocol.getCommitMessage(getName(), "receiver");
				Request commitMessageWitness = TwoPhaseProtocol.getCommitMessage(getName(), "witness");
				DriverProgram.requestQueue.add(commitMessageReceiver);
				DriverProgram.requestQueue.add(commitMessageWitness);
				iterator = DriverProgram.requestQueue.iterator();
				boolean receiverCommit = false, witnessCommit = false;
				while(iterator.hasNext() && (!receiverCommit || !witnessCommit)) {
					Request temp = iterator.next();
					if(temp.getRecipient() == getName() && temp.getRequestCode() == RequestCodes.TWO_PHASE) {
						String messageFrom = temp.getSender();
						if(messageFrom.equals("receiver")) {
							iterator.remove();
							if(temp.getMessage().equals(String.valueOf(MessageCodes.ACK_COMMIT)))
								receiverCommit = true;
							else
								break;
						}
						else if(messageFrom.equals("witness")) {
							if(temp.getMessage().equals(String.valueOf(MessageCodes.ACK_COMMIT)))
								witnessCommit = true;
							else
								break;
						}
					}
				}
				if(!receiverCommit || !witnessCommit) {
					System.out.println("The transaction can not be committed as receiver of sender has not acknowledged");
				}
				else {
					System.out.println("Transaction can be commited");
				}
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

}
