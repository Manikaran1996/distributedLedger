package node;

import java.security.PrivateKey;
import java.util.LinkedList;
import transaction.Transaction;
import transaction.TransactionManager;
import transaction.UTXO;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
public class DriverProgram {
	
	private HashMap<String,String> hashMap = new HashMap<String, String>();
	private LinkedList<Transaction> txnList;
	public DriverProgram() {
		txnList = new LinkedList<Transaction>();
	}
	
	public static void main(String[] args) {
		// instead of making every function static I have made an object of the class
		// and called the functions on the object
		final int id = Integer.parseInt(args[0]);
		int txnId =0;
		TransactionManager.initialize();
		DriverProgram dp = new DriverProgram();
		//creating the NodeThread using name given on command line 
		NodeThread myThread = new NodeThread(args[0],args[1]);
		PrivateKey pk = myThread.priKey;
		System.out.println("Starting ...");
		try {
			Thread.currentThread().sleep(500*(Integer.parseInt(args[2])));
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		myThread.broadcast.broadcastTransaction((Transaction.getDummyTransaction(myThread.pubKey, 10.0,++txnId,args[0])));			
		System.out.println("Command format");
		System.out.println("1. 1 x -> Find public key of x (Task 1)");
		System.out.println("2. 2 x y z -> send z bitcoins to x with y as witness (Task 2,3,4)");
		System.out.println("3. 3 -> print transaction list hash (Task 5)");
		System.out.println("4. 4 -> Check balance");
		System.out.println("5. 5 -> Do concurrent transaction (Task 5) ");
		System.out.println("6. 6 -> Try double spend (Task 7)");
		
		Scanner s=new Scanner(System.in);
		while(s.hasNext()) {
			String node=s.next();
			if(node.equals("1")) {
				node=s.next();
				if(!myThread.threadMap.containsKey(node)) {
					System.out.println("Can't find public key of "+node +" since it is not running");
					continue;
				}
				System.out.print("Public key of "+node+ ":");
				System.out.println(myThread.dht.getValue(node));
			}
			else if(node.equals("2")){
				String rec=s.next();
				String wit=s.next();
				Double amtDouble=s.nextDouble();
				if(!myThread.threadMap.containsKey(rec)) {
					System.out.println(rec+" is not running");
					continue;
				}
				if(!myThread.threadMap.containsKey(wit)) {
					System.out.println(wit+" is not running");
					continue;
				}
				Transaction transaction=TransactionManager.createTransaction(id,txnId, args[0], rec,wit,myThread.pubKey,myThread.dht.getValue(rec), amtDouble, pk);
				if(transaction!=null) new RequestTransaction("request transaction", transaction, myThread.threadMap,myThread.broadcast);
			}
			else if(node.equals("3")){
				System.out.println(TransactionManager.getHashCode());
			}
			else if(node.equals("4")) {
				System.out.println("Balance : "+UTXO.getBalance(myThread.pubKey));
			}
			else if(node.equals("5")) {
				System.out.println("Type x y z to send z coins to x with y as witness \nThe transaction will begin within a random interval after input ");
				String rec=s.next();
				String wit=s.next();
				Double amtDouble=s.nextDouble();
				if(!myThread.threadMap.containsKey(rec)) {
					System.out.println(rec+" is not running");
					continue;
				}
				if(!myThread.threadMap.containsKey(wit)) {
					System.out.println(wit+" is not running");
					continue;
				}
				try {
					Random random=new Random();
					Thread.currentThread().sleep((int)(random.nextDouble()*5000)); 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Transaction transaction=TransactionManager.createTransaction(id,txnId, args[0], rec,wit,myThread.pubKey,myThread.dht.getValue(rec), amtDouble, pk);
				if(transaction!=null) new RequestTransaction("request transaction", transaction, myThread.threadMap,myThread.broadcast);
			}
			else if(node.equals("6")) {
				System.out.println("Type x y z to send z coins to x with y as witness in next two lines as two transactions in double spend");
				String rec=s.next();
				String wit=s.next();
				Double amtDouble=s.nextDouble();
				Transaction transaction=TransactionManager.createTransaction(id,txnId, args[0], rec,wit,myThread.pubKey,myThread.dht.getValue(rec), amtDouble, pk);
				rec=s.next();
				wit=s.next();
				amtDouble=s.nextDouble();
				Transaction transaction1=TransactionManager.createTransaction(id,txnId, args[0], rec,wit,myThread.pubKey,myThread.dht.getValue(rec), amtDouble, pk);
				if(transaction!=null) new RequestTransaction("request transaction", transaction, myThread.threadMap,myThread.broadcast);
				if(transaction1!=null) new RequestTransaction("request transaction", transaction1, myThread.threadMap,myThread.broadcast);

			}
			System.out.println("Command format");
			System.out.println("1. 1 x -> Find public key of x (Task 1)");
			System.out.println("2. 2 x y z -> send z bitcoins to x with y as witness (Task 2,3,4)");
			System.out.println("3. 3 -> print transaction list hash (Task 5)");
			System.out.println("4. 4 -> Check balance");
			System.out.println("5. 5 -> Do concurrent transaction (Task 5) ");
			System.out.println("6. 6 -> Try double spend (Task 6)");
			
		}
		/* change accordingly if required
		dp.initialize();
		for(int i=1;i<=5;i++) {
			NodeThread temp = dp.hashMap.get(String.valueOf(i));
			temp.copyList(dp.txnList);
			temp.start();
		}
		Request request = new Request();
		request.setRequestCode(RequestCodes.COMMIT);
		sender.putRequest(request);
		//sender.commitTransaction(); */
	}

	private void initialize() {
		int mapSize = hashMap.size();
		long txId = 1;
		for(int i=1;i<=mapSize;i++) {
			//update when changed
			//txnList.add(Transaction.getDummyTransaction(hashMap.get(String.valueOf(i)).pubKey, 100.0, txId , String.valueOf(i)));
			txId++;
			
		}
	}
	
}
