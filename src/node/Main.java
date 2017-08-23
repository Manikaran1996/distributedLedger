package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import transaction.Transaction;
import transaction.TransactionManager;

public class Main {
	public static void main(String[] args) {
		final int id = Integer.parseInt(args[0]);
		final int numOfNodes = 4;
		final int ports[] = new int[] {5555,7777,8888,9999};
		int txnId = 1;
		for(int i=1;i<=numOfNodes;i++) {
			TransactionManager.addTransaction(Transaction.getDummyTransaction(null, 100.0, txnId , String.valueOf(i)));
			txnId++;
			
		}
		Scanner sc = new Scanner(System.in);
		int choice =1;
		while(choice != 5) {
			System.out.println("1. Send Bitcoins\n2. Query public key\n3. Display the hash Code of Txn. list");
			choice = sc.nextInt();
			switch(choice) {
			case 1:
				System.out.println("Enter the receiver's account number : ");
				int rec = sc.nextInt();
				System.out.println("Enter the account number of witness : ");
				int wit = sc.nextInt();
				System.out.println("Enter the amount to be sent : ");
				double amt = sc.nextDouble();
				//DHT dht = new DHT();
				Transaction txn = TransactionManager.createTransaction(id, txnId, String.valueOf(id), 
						String.valueOf(rec), String.valueOf(wit), null, null, amt);
				
				
				// for testing
				Socket receiver = null, witness = null;
				try {
					receiver = new Socket("localhost", ports[rec]);
					witness = new Socket("localhost", ports[wit]);
					ObjectOutputStream recOut = new ObjectOutputStream(receiver.getOutputStream());
					ObjectInputStream recIn = new ObjectInputStream(receiver.getInputStream());
					ObjectOutputStream witOut = new ObjectOutputStream(witness.getOutputStream());
					ObjectInputStream witIn = new ObjectInputStream(witness.getInputStream());
					recOut.writeObject(txn);
					witOut.writeObject(txn);
				}
				catch(Exception e) {
					e.getStackTrace();
				}
				finally {
					try {
						if(receiver != null)
							receiver.close();
						if(witness != null)
							witness.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				break;
			case 2:
				//DHT handle
				break;
			case 3:
				System.out.println(TransactionManager.getHashCode());
				break;
			}
		}
		sc.close();
	}
}
