package node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			if(res) {
				//System.out.println("Ready for Two phase");
				byte[] b = new byte[100];
				//inStream.read(b);
				//String reply = new String(b); 
				int rep = inStream.readInt();
				System.out.println(rep);
				if(rep == 1) {
					//System.out.println(getName() + " Received Prepare message");
					outStream.writeInt(2);
					outStream.flush();
					int reply = inStream.readInt();
					if(reply == 3) {
						System.out.println(getName() + " Received Commit message");
						outStream.writeInt(4);
						outStream.flush();
						TransactionManager.addTransaction(t);
					}
				}
				else {

					System.out.println(getName() + "Received Prepare message1");
				}
			}
			else {
				if(inStream.readUTF().equals("PREPARE")) {

					//System.out.println(getName() + "Received Prepare message");
					outStream.writeUTF("NO");
					if(inStream.readUTF().equals("ABORT")) {
						outStream.writeUTF("ACK");
					}
				}
				
			}
		
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
}
