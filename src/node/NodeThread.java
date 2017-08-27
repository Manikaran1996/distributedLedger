package node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import node.Request.RequestCodes;
import transaction.Transaction;

public class NodeThread extends Thread {
	public static final String FILENAME="/home/mininet/project/init.txt";
	public static int PORT=5555; // change as required 
	final int ports[] = new int[] {5555,5557,8888,9999};
	private LinkedList<Transaction> txnList;
	String pubKey,priKey; //added data member to store keys
	Security security; //to use hash/key generation
	HashMap<String, String> threadMap; // now threadMap stores name->IP address mapping
	//private CommitThread commitThread; add when changed
	public DHT dht;
	//The linkedList of Transaction contains the initial transactions which needs to be copied to the transaction list of the node
	public NodeThread(String threadName) {
		super(threadName);
		txnList = new LinkedList<Transaction>();
		//commitThread = null;
		security=new Security();
		pubKey=security.getPublicKey();
		priKey=security.getPrivateKey();
		buildThreadMap();
		dht=new DHT(threadName, threadMap);
		start();
	}
	
	public NodeThread(String threadName, int id) {
		super(threadName);
		txnList = new LinkedList<Transaction>();
		//commitThread = null;
		security=new Security();
		pubKey=security.getPublicKey();
		priKey=security.getPrivateKey();
		buildThreadMap();
		PORT = ports[id];
		dht=new DHT(threadName, threadMap);
		start();
	}
	
	private void buildThreadMap() {
		FileReader fr;
		threadMap=new HashMap<String, String>();
		try {
			fr = new FileReader(new File(FILENAME));
			BufferedReader br=new BufferedReader(fr);
			String cLineString;
			while((cLineString=br.readLine())!=null) {
				String _t[]=cLineString.split(":");
				threadMap.put(_t[0], _t[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void run() {
		dht.putValue(getName(),pubKey);;
		ServerSocket mailBox=null;
		try {
			mailBox=new ServerSocket(PORT);
			System.out.println(getName() + " : listening on " + PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//printTransactionTable(); uncomment when update 
		do {
			Request request;
			try {
				Socket client=mailBox.accept();
				Thread.sleep(5000);
				ObjectInputStream inputStream=new ObjectInputStream(client.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				out.flush();
				request=(Request)inputStream.readObject();
				//inputStream.close();
				if(request.getRequestCode() == RequestCodes.COMMIT) {
					commitTransaction("receiver", "witness");
				}
				
				else if(request.getRequestCode() == RequestCodes.TRANSACTION) {
					System.out.println(getName() + " : Request Received");
					Thread.sleep(5000);
					Transaction t = (Transaction) inputStream.readObject();
					//System.out.println(t);
					new ServiceTransaction("requestHandler", t);
				}
				
				else if(request.getRequestCode()==RequestCodes.SEARCH) {
					new DHThread("DHThread",getName(),dht,client,request);
				}
				else if(request.getRequestCode() == RequestCodes.TWO_PHASE) {
					System.out.println("Two Phase Request Received");
					new TwoPhaseCommitHandler("2PhaseHandler", inputStream, out);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} while(true);
	}

	private void commitTransaction(String rec, String wit) { 
		//update when changed
		//commitThread = new CommitThread(getName(), rec, wit, this, threadMap.get(rec), threadMap.get(wit));
		
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
