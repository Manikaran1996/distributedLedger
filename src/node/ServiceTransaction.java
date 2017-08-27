package node;

import transaction.Transaction;
import transaction.TransactionManager;

public class ServiceTransaction extends Thread {

	Transaction t;
	public ServiceTransaction(String threadName, Transaction transaction) {
		super(threadName);
		t = transaction;
		start();
	}
	
	public void run() {
		boolean verified = TransactionManager.verifyTransaction(t);
		if(verified) {
			TransactionManager.addTransaction(t);
			System.out.println(getName() + " : Transaction verified ");
		}
		else {
			System.out.println("Transaction Not verified");
		}
		
	}
}
