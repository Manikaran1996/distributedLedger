package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import transaction.Transaction;
import transaction.TransactionManager;

public class ServiceTransaction extends Thread {

	Socket client;
	public ServiceTransaction(String threadName, Socket clientSocket) {
		super(threadName);
		client = clientSocket;
		start();
	}
	
	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			Transaction t = (Transaction) in.readObject();
			boolean verified = TransactionManager.verifyTransaction(t);
			if(verified) {
				TransactionManager.addTransaction(t);
			}
			else {
				System.out.println("Transaction Not verified");
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
