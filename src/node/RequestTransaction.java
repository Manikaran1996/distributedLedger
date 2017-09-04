package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.naming.spi.DirStateFactory.Result;

import node.Request.RequestCodes;
import transaction.Transaction;
import transaction.TransactionManager;

public class RequestTransaction extends Thread {
	Transaction txn;
	HashMap<String, String> threadMap;
	Broadcast broadcast;
	private Object lock;
	public RequestTransaction(String threadName, Transaction t,HashMap<String, String> hm,Broadcast b) {
		super(threadName);
		txn = t;
		threadMap=hm;
		broadcast=b;
		lock=new Object();
		start();
	}
	public static class Result {
				private boolean res;
				public void setTrue() {
					res = true;
				}
				public void setFalse() {
					res = false;
				}
				public boolean getValue() {
					return res;
		 		}
		 	}
	public void run() {
			//System.out.println(rec);
			// Two phase start
			Socket receiver = null, witness = null;
			try {
				Request req = new Request();
				req.setRequestCode(RequestCodes.TWO_PHASE);
				//System.out.println(txn);
				receiver = new Socket(threadMap.get(txn.getReceiver()), NodeThread.PORT);
				ObjectOutputStream recOut = new ObjectOutputStream(receiver.getOutputStream());
				recOut.writeObject(req);
				recOut.flush();
				witness = new Socket(threadMap.get(txn.getWitness()), NodeThread.PORT);
				ObjectOutputStream witOut = new ObjectOutputStream(witness.getOutputStream());
				witOut.writeObject(req);
				witOut.flush();
				receiver.setSoTimeout(15000);
				witness.setSoTimeout(15000);
				witOut.flush();
				ObjectInputStream recIn = new ObjectInputStream(receiver.getInputStream());
				//
				
				ObjectInputStream witIn = new ObjectInputStream(witness.getInputStream());
				
				
				//recOut.flush();
				//System.out.println("Request sent");
				Result result = new Result();
				result.setFalse();
				new TwoPhaseCommit("2Phase", recOut, witOut, recIn, witIn, txn, result,lock);
				synchronized(lock) {
				lock.wait();
				}
				if(result.getValue()) {
					broadcast.broadcastTransaction((txn));
					//System.out.println("Transaction broadcasted");
					}
				else {
				System.out.println("Aborted");
				}
				recIn.close();
				witIn.close();
				recOut.close();
				witOut.close();
				/*Request r1=(Request)recIn.readObject();
				Request r2=(Request) witIn.readObject();
				if(r1.getMessage().equals("YES") && r2.getMessage().equals("YES")) broadcast.broadcastTransaction(txn);
				else System.out.println("/* Receiver or witness aborted 2phase */
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
			//Thread.sleep(1000);
			// TODO Two phase to be completed
			// broadcast
			
			//recIn.close();
			//witIn.close();
			//recOut.close();
			//witOut.close();
		
	}
	
	
}
