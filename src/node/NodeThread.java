package node;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.HashMap;
import node.Request.RequestCodes;
import transaction.Transaction;
import transaction.TransactionManager;

public class NodeThread extends Thread {
	public static final int PORT=6666; // change as required 
	String pubKey;
	PrivateKey priKey; //added data member to store keys
	Security security; //to use hash/key generation
	HashMap<String, String> threadMap; // now threadMap stores name->IP address mapping
	//private CommitThread commitThread; add when changed
	public DHT dht;
	Broadcast broadcast;
	private String IP;
	private String nodeName;
	//The linkedList of Transaction contains the initial transactions which needs to be copied to the transaction list of the node
	public NodeThread(String threadName,String myIP) {
		super(threadName);
		//commitThread = null;
		security=new Security();
		pubKey=security.getPublicKey();
		priKey=security.getPrivateKey();
		IP=myIP;
		nodeName=threadName;
		threadMap=new HashMap<String, String>();
		threadMap.put(nodeName,IP);
		dht=new DHT(threadName, threadMap,this);
		broadcast=new Broadcast("br thread", threadMap, dht, threadName,this);
		start();
	}
	
	public void run() {
		Request m=new Request();
		m.setSender(nodeName);
		m.setRequestCode(RequestCodes.INIT);
		m.setMessage(IP);
		broadcast.broadcastMessage(m);	
		ServerSocket mailBox=null;
		try {
			mailBox=new ServerSocket(PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dht.putValue(nodeName,pubKey);
		//printTransactionTable(); uncomment when update 
		do {
			Request request;
			try {
				Socket client=mailBox.accept();
				
				ObjectInputStream inputStream=new ObjectInputStream(client.getInputStream());
				request=(Request)inputStream.readObject();
				if(request.getRequestCode() == RequestCodes.TRANSACTION) {
					Transaction t = (Transaction) inputStream.readObject();
					new ServiceTransaction("requestHandler", t);
				}
				else if(request.getRequestCode()==RequestCodes.ADD_KEY) {
					String temp[]=request.getMessage().split(":");
					dht.dHTableHashMap.put(temp[0],temp[1]);
				}
				else if(request.getRequestCode()==RequestCodes.SEARCH) {
					new DHThread("DHThread",getName(),dht,client,request);
				}

				else if(request.getRequestCode() == RequestCodes.TWO_PHASE) {
					//System.out.println("Two Phase Request Received");
					ObjectOutputStream out=new ObjectOutputStream(client.getOutputStream());
					new TwoPhaseCommitHandler("2PhaseHandler", inputStream, out);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} while(true);
	}

}
