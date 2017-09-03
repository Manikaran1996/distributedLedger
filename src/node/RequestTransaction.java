package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import node.Request.RequestCodes;
import transaction.Transaction;
import transaction.TransactionManager;

public class RequestTransaction extends Thread {
	final int ports[] = new int[] {5555,5557,8888,9999};
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
			System.out.println(rec);
			// Two phase start
			Socket receiver = null, witness = null;
			try {
				receiver = new Socket("localhost", ports[rec]);
				witness = new Socket("localhost", ports[wit]);
				ObjectOutputStream recOut = new ObjectOutputStream(receiver.getOutputStream());
				recOut.flush();
				ObjectInputStream recIn = new ObjectInputStream(receiver.getInputStream());
				ObjectOutputStream witOut = new ObjectOutputStream(witness.getOutputStream());
				witOut.flush();
				ObjectInputStream witIn = new ObjectInputStream(witness.getInputStream());
				Request req = new Request();
				req.setRequestCode(RequestCodes.TWO_PHASE);
				recOut.writeObject(req);
				witOut.writeObject(req);
				System.out.println("Request Sent");
				Boolean result = new Boolean(false);
				TwoPhaseCommit tpc = new TwoPhaseCommit("2Phase", recOut, witOut, 
						recIn, witIn, txn, result);
				tpc.join();
				if(result) {
					System.out.println("Transaction sent");
				}
				Main.txnId++;
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
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
			Thread.sleep(1000);
			// TODO Two phase to be completed
			// broadcast
			
			TransactionManager.addTransaction(txn);
			receiver.close();
			witness.close();
			//recIn.close();
			//witIn.close();
			//recOut.close();
			//witOut.close();
		
		
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
