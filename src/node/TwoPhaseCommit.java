package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;

import transaction.Transaction;

public class TwoPhaseCommit extends Thread {
	
	private ObjectOutputStream recOutStream, witOutStream;
	private ObjectInputStream recInStream, witInStream;
	private Boolean done;
	private Transaction txn;
	public TwoPhaseCommit(String name, ObjectOutputStream recO, ObjectOutputStream witO, 
			ObjectInputStream recI, ObjectInputStream witI, Transaction t, Boolean res) {
		super(name);
		recOutStream = recO;
		recInStream = recI;
		witOutStream = witO;
		witInStream = witI;
		txn = t;
		done = res;
		start();
	}
	
	public void run() {
		// get the port number and the ip address of receiver and the witness
		try {
			recOutStream.writeObject(txn);
			witOutStream.writeObject(txn);
			//Thread.sleep(2000); // waiting for receiver and witness to verify the transaction
			// communication
			byte[] rec = new byte[100], wit = new byte[100];
			recOutStream.writeInt(1);
			witOutStream.writeInt(1);
			recOutStream.flush();
			witOutStream.flush();
			//recOutStream.writeUTF("PREPARE");
			//witOutStream.writeUTF("PREPARE");
			System.out.println("Prepare Message Sent");
			/*recInStream.read(rec);
			witInStream.read(wit);
			String recReply = new String(rec);
			String witReply = new String(wit);
			System.out.println(recReply + witReply);*/
			
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
					//TODO broadcast the transaction here
					done = true;
					System.out.println("Transaction Committed!!");
				}
				
			}
			
			/*
			
			if(recReply.toUpperCase().equals("READY") && witReply.toUpperCase().equals("READY")) {
				System.out.println("Ready Message Received");
				Thread.sleep(2000);
				recOutStream.write("COMMIT".getBytes());
				witOutStream.write("COMMIT".getBytes());
				recOutStream.flush();
				witOutStream.flush();
				System.out.println("Commit Message Sent");
				Thread.sleep(2000);
				recInStream.read(rec);
				witInStream.read(wit);
				recReply = new String(rec);
				witReply = new String(wit);
				if(recReply.toUpperCase().equals("ACK") && witReply.toUpperCase().equals("ACK")) {
					//TODO broadcast the transaction here
					done = true;
					System.out.println("Transaction Committed!!");
				}
				System.out.println("Transaction aborted21!!");
			}
			else {
				recOutStream.write("ABORT".getBytes());
				witOutStream.write("ABORT".getBytes());
				recOutStream.flush();
				witOutStream.flush();
				recInStream.read(rec);
				witInStream.read(wit);
				recReply = new String(rec);
				witReply = new String(wit);
				if(recReply.toUpperCase().equals("ACK") && witReply.toUpperCase().equals("ACK")) {
					System.out.println("Transaction aborted!!");
				}
				done = false;
				
			}*/
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	
	}
	

}