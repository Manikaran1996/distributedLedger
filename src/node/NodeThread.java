package node;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import node.Request.RequestCodes;
import node.TwoPhaseProtocol.MessageCodes;

public class NodeThread extends Thread {
	
	private BlockingQueue<Request> mailBox;
	String DHTprev="";
	String DHTnext="";
	String DHTcurr="";
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
		DHTcurr=getName();
		security=new Security();
		pubKey=security.getPublicKey();
		priKey=security.getPrivateKey();
		}
	

	
	public void run() {
		/*try {
			synchronized(DriverProgram.requestQueue) {
				DriverProgram.requestQueue.wait();
				Request r = DriverProgram.requestQueue.peek();
				if(r.getRequestCode() == RequestCodes.DEFAULT) {
					System.out.println("Hello I am a thread and my name is " + getName());
				}
			getDHTValue("Node"+(DHTcurr+4));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		putDHTValue(DHTcurr,pubKey);
		do {
			Request request;
			try {
				request = mailBox.take();
				if(request.getRequestCode() == RequestCodes.COMMIT) {
					commitTransaction("receiver", "witness");
				}
				else if(request.getRequestCode()==RequestCodes.ADD_KEY) {
					DHTable.put(request.getSender(), request.getMessage());
					System.out.println(getName() + " added public key of "+request.getSender()+" as "+request.getMessage());
				}
				else if(request.getRequestCode()==RequestCodes.SEARCH) {
					String s=DHTable.get(request.getMessage());
					Request replyRequest=new Request();
					replyRequest.setRequestCode(RequestCodes.SEARCH_REPLY);
					replyRequest.setSender(DHTcurr);
					replyRequest.setRecipient(request.getSender());
					replyRequest.setMessage(request.getMessage()+":"+s);
					threadMap.get(replyRequest.getRecipient()).putRequest(replyRequest);
				}
				else if(request.getRequestCode()==RequestCodes.SEARCH_REPLY) {
					String reply[]=request.getMessage().split(":");
					System.out.println("at" + DHTcurr +" received public key of "+reply[0]+" from "+request.getSender()+" as "+reply[1]);
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
	
	private String DHTFindNode(String keyVal) {
		String currNode=DHTcurr;
		BigInteger keyValHash=new BigInteger(security.bytesToString(security.getHash(keyVal)),16);
		BigInteger aBigInteger=new BigInteger(security.bytesToString(security.getHash(currNode)),16);
		BigInteger bBigInteger=new BigInteger(security.bytesToString(security.getHash(threadMap.get(currNode).DHTnext)),16);
		do{
			if(keyValHash.compareTo(aBigInteger)==0) return currNode;
			if(aBigInteger.compareTo(bBigInteger)<0 && keyValHash.compareTo(bBigInteger)<0 && aBigInteger.compareTo(keyValHash)<0) return threadMap.get(currNode).DHTnext;
			if(aBigInteger.compareTo(bBigInteger)>0 && (keyValHash.compareTo(aBigInteger)>0 || (keyValHash.compareTo(aBigInteger)<0 && keyValHash.compareTo(bBigInteger)<0))) return threadMap.get(currNode).DHTnext;
			currNode=threadMap.get(currNode).DHTnext;
			aBigInteger=new BigInteger(security.bytesToString(security.getHash(currNode)),16);
			bBigInteger=new BigInteger(security.bytesToString(security.getHash(threadMap.get(currNode).DHTnext)),16);
		}while(true);
		}
	synchronized public void getDHTValue(String keyVal) {
		String reqNode=DHTFindNode(keyVal);
		Request r =new Request();
		r.setRequestCode(RequestCodes.SEARCH);
		r.setSender(DHTcurr);
		r.setRecipient(reqNode);
		r.setMessage(keyVal);
		threadMap.get(reqNode).putRequest(r);
		//String temp= threadMap.get(reqNode).DHTable.get(keyVal);
		//System.out.println(reqNode + "returns value of "+keyVal+ " as "+temp);
		//return temp;
		}
	synchronized public void putDHTValue(String key,String Value) {
		String reqNode=DHTFindNode(key);
		Request r= new Request();
		r.setRequestCode(RequestCodes.ADD_KEY);
		r.setRecipient(reqNode);
		r.setSender(key);
		r.setMessage(Value);
		threadMap.get(reqNode).putRequest(r);//threadMap.get(reqNode).DHTable.put(keyVal,Value);
		//System.out.println(reqNode + "stored value of "+DHTcurr+ " as "+Value);
		} 
	
	public String query(String nodeName) {
		return null;
	}
	
	public String verify(Transaction t) {
		return null;
	}
	
	/* public boolean commitTransaction() {
		// Start Two phase protocol
		Request prepareMessageReceiver = TwoPhaseProtocol.getPrepareMessage(getName(), "receiver"); // hard-coding for now, will be using DHT.
		Request prepareMessageWitness = TwoPhaseProtocol.getPrepareMessage(getName(), "witness"); // hard-coding for now, will be using DHT.
		NodeThread receiver = threadMap.get("receiver");
		NodeThread witness = threadMap.get("witness");
		receiver.putRequest(prepareMessageReceiver);
		witness.putRequest(prepareMessageWitness);
		boolean receiverReady = false, witnessReady = false;
		Thread.sleep(2000);
		Request temp = mailBox.poll(5, TimeUnit.SECONDS);
		if(temp == null) {
			//abort
		}
		else {
			if(temp.getRecipient() == getName()) {
				if(temp.getRequestCode() == RequestCodes.TWO_PHASE) {
					if(temp.getMessage().equals(String.valueOf(MessageCodes.READY)))
						receiverReady = true;
				}
				else {
					// take the required action
				}
			}
		}
		while(iterator.hasNext() && (!receiverReady || !witnessReady)) {
				Request temp = iterator.next();
				if(temp.getRecipient() == getName() && temp.getRequestCode() == RequestCodes.TWO_PHASE) {
					String messageFrom = temp.getSender();
					if(messageFrom.equals("receiver")) {
						iterator.remove();
						
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
	} */
	
	private void commitTransaction(String rec, String wit) { 
		commitThread = new CommitThread(getName(), rec, wit, this, threadMap.get(rec), threadMap.get(wit));
		
	}

}
