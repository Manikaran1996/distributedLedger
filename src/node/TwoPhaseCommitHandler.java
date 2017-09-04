package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import transaction.Transaction;
import transaction.TransactionManager;

public class TwoPhaseCommitHandler extends Thread {
	
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	public TwoPhaseCommitHandler(String name, ObjectInputStream in, ObjectOutputStream out) {
		super(name);
		inStream = in;
		outStream = out;
		start();
	}
	
	public void run() {
		try {
			Transaction t = (Transaction) inStream.readObject();
			
			//System.out.println("Transaction Object read");
			boolean res = TransactionManager.verifyTransaction(t);
			Random random=new Random();
			double p=random.nextDouble();
			if(p<=0.05) res=false;
			
			int rep=inStream.readInt();

			//System.out.println(getName() + " : " + res + " : " + rep);
			if(res) {
				//System.out.println("Ready for Two phase");
				if(rep == 1) {
					//System.out.println(getName() + " Received Prepare message");
					outStream.writeInt(2);
					outStream.flush();
					int reply = inStream.readInt();
					if(reply == 3) {
						//System.out.println(getName() + " Received Commit message");
						outStream.writeInt(4);
						outStream.flush();
					}
					else if(reply == 6) {
						outStream.writeInt(7);
						outStream.flush();
					}
				}
				else {
				}
			}
			else {
				if(rep == 1) {
					outStream.writeInt(5);
					outStream.flush();
					int reply = inStream.readInt();
					if(reply == 6) {
					outStream.writeInt(7);
					outStream.flush();
				}
				}
				
			}
		inStream.close();
		outStream.close();
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
}
