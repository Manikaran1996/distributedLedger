package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import node.RequestTransaction.Result;
import transaction.Transaction;

public class TwoPhaseCommit extends Thread {
	
	private ObjectOutputStream recOutStream, witOutStream;
	private ObjectInputStream recInStream, witInStream;
	private Result done;
	private Transaction txn;
	private Object lock;
	public TwoPhaseCommit(String name, ObjectOutputStream recO, ObjectOutputStream witO, 
			ObjectInputStream recI, ObjectInputStream witI, Transaction t, Result res, Object lock) {
		super(name);
		recOutStream = recO;
		recInStream = recI;
		witOutStream = witO;
		witInStream = witI;
		txn = t;
		done = res;
		this.lock = lock;
		start();
	}
	
	public void run() {
		// get the port number and the ip address of receiver and the witness
		try {
			recOutStream.writeObject(txn);
			witOutStream.writeObject(txn);
			//Thread.sleep(2000); // waiting for receiver and witness to verify the transaction
			// communication
			recOutStream.writeInt(1);
			witOutStream.writeInt(1);
			recOutStream.flush();
			witOutStream.flush();
			System.out.println("Prepare Message Sent");
			
			int prepReplyRec = recInStream.readInt();
			int prepReplyWit = witInStream.readInt();
			
			if(prepReplyRec == 2 && prepReplyWit == 2) {
				System.out.println("Ready Message Received");
				recOutStream.writeInt(3);
				witOutStream.writeInt(3);
				recOutStream.flush();
				witOutStream.flush();
				System.out.println("Commit Message Sent");
				int recReply = recInStream.readInt();
				int witReply = witInStream.readInt();
				if(recReply == 4 && witReply == 4) {
					synchronized(lock) {
						done.setTrue();
						lock.notify();
					}
					System.out.println("Transaction Committed!!");
				}				
			}
			else {
				recOutStream.writeInt(6);	// send abort
				witOutStream.writeInt(6);	// send abort
				recOutStream.flush();
				witOutStream.flush();
				int recReply = recInStream.readInt();
				int witReply = witInStream.readInt();
				if(recReply == 7 && witReply == 7) {
					synchronized(lock) {
						done.setFalse();
						lock.notify();
					}
					System.out.println("Transaction aborted!!");
				}			
			}
		
		} catch (UnknownHostException e) {
			done.notify();
			e.printStackTrace();
		} catch(SocketTimeoutException e) {
			done.notify();
			
		} catch (IOException e) {
			done.notify();
			e.printStackTrace();
		} 
	
	}
	

}
