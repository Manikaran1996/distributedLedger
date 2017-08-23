package node;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import node.Request.RequestCodes;
import transaction.Transaction;

public class NodeThread extends Thread {
	public static final int PORT=6666; // change as required 
	private LinkedList<Transaction> txnList;
	private long txnId;
	String pubKey,priKey; //added data member to store keys
	Security security; //to use hash/key generation
	HashMap<String, String> threadMap; // now threadMap stores name->IP address mapping
	//private CommitThread commitThread; add when changed
	public DHT dht;
	//The linkedList of Transaction contains the initial transactions which needs to be copied to the transaction list of the node
	public NodeThread(String threadName, HashMap<String, String> map) {
		super(threadName);
		txnList = new LinkedList<Transaction>();
		threadMap = map;
		//commitThread = null;
		security=new Security();
		pubKey=security.getPublicKey();
		priKey=security.getPrivateKey();
		dht=new DHT(threadName, map);
		start();
	}
	

	
	public void run() {
		dht.putValue(getName(),pubKey);;
		ServerSocket mailBox=null;
		try {
			mailBox=new ServerSocket(PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//printTransactionTable(); uncomment when update 
		do {
			Request request;
			try {
				Socket client=mailBox.accept();
				
				ObjectInputStream inputStream=new ObjectInputStream(client.getInputStream());
				request=(Request)inputStream.readObject();
				if(request.getRequestCode() == RequestCodes.COMMIT) {
					commitTransaction("receiver", "witness");
				}
				else if(request.getRequestCode()==RequestCodes.SEARCH) {
					new DHThread("DHThread",getName(),dht,client,request);
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
	
	public String verify(Transaction t) {
		return null;
	}
	
	private void commitTransaction(String rec, String wit) { 
		//update when changed
		//commitThread = new CommitThread(getName(), rec, wit, this, threadMap.get(rec), threadMap.get(wit));
		
	}

	public void copyList(LinkedList<Transaction> initialList) {
		Iterator<Transaction> it = initialList.iterator();
		while(it.hasNext()) {
			txnList.add(it.next());
		}
		txnId = initialList.size()+1; // plus 1 because the next txn id should be 1 more than the last transaction's id
	}
	
	private void printTransactionTable() {
		/*Iterator<Transaction> it = txnList.iterator();
		while(it.hasNext()) {
			System.out.println("Thread Number : " + getName());
			System.out.println(it.next());
			System.out.println();
		}*/
		System.out.println("Thread Number : " + getName() + "\n Hash : " + txnList.hashCode());
	}
}
