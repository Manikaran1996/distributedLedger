package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import transaction.Transaction;

public class RequestTransaction extends Thread {
	final int ports[] = new int[] {5555,7777,8888,9999};
	Transaction txn;
	public RequestTransaction(String threadName, Transaction t) {
		super(threadName);
		txn = t;
		start();
	}
	
	public void run() {
		int rec = Integer.parseInt(txn.getReceiver());
		int wit = Integer.parseInt(txn.getWitness());
		try {
			
			// Two phase start
			Socket receiver = new Socket("localhost", ports[rec]);
			Socket witness = new Socket("localhost", ports[wit]);
			ObjectOutputStream recOut = new ObjectOutputStream(receiver.getOutputStream());
			ObjectInputStream recIn = new ObjectInputStream(receiver.getInputStream());
			ObjectOutputStream witOut = new ObjectOutputStream(witness.getOutputStream());
			ObjectInputStream witIn = new ObjectInputStream(witness.getInputStream());
			recOut.writeObject(txn);
			witOut.writeObject(txn);
			Thread.sleep(1000);
			// TODO Two phase to be completed
			// broadcast
			
			receiver.close();
			witness.close();
			recIn.close();
			witIn.close();
			recOut.close();
			witOut.close();
		
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
