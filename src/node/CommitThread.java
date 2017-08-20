package node;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import node.TwoPhaseProtocol.MessageCodes;
/* uncomment when changed
public class CommitThread extends Thread {

	String committer, receiver, witness;
	NodeThread receiverNode, witnessNode, committerNode;
	BlockingQueue<Request> mailBox;
	public CommitThread(String commit, String rec, String wit, NodeThread committerNode, NodeThread receiverNode, NodeThread witnessNode) {
		super();
		committer = commit;
		receiver = rec;
		witness = wit;
		this.receiverNode = receiverNode;
		this.witnessNode = witnessNode;
		this.committerNode = committerNode;
		mailBox = new LinkedBlockingQueue<Request>();
		start();
	}
	
	public void run() {
		Date timeCounter = new Date();
		Request prepareMessageReceiver = TwoPhaseProtocol.getPrepareMessage(committer, receiver);
		Request prepareMessageWitness = TwoPhaseProtocol.getPrepareMessage(committer, witness);
		receiverNode.putRequest(prepareMessageReceiver);
		witnessNode.putRequest(prepareMessageWitness);
		long start = timeCounter.getTime();
		long timeout = 10000;
		try {
			Request reply1 = mailBox.poll(timeout, TimeUnit.MILLISECONDS);
			if(reply1 == null) {
				System.out.println("Aborting the commit transaction\nTime out has occurred");
				// inform the committer thread
			}
			else {
				timeout -= (timeCounter.getTime() - start);
				Request reply2 = mailBox.poll(timeout, TimeUnit.MILLISECONDS);
				if(reply2 == null) {
					System.out.println("Aborting the commit transaction\nTime out has occurred");
					// inform the committer thread
				}
				else {
					boolean receiverReady = false, witnessReady = false;
					if(reply1.getSender() == receiver) {
						if(reply1.getMessage() == String.valueOf(MessageCodes.READY))
							receiverReady = true;
						else
							receiverReady = false;
					}
					else if(reply1.getSender() == witness) {
						if(reply1.getMessage() == String.valueOf(MessageCodes.READY))
							witnessReady = true;
						else
							witnessReady = false;
					}
					if(reply2.getSender() == receiver) {
						if(reply2.getMessage() == String.valueOf(MessageCodes.READY))
							receiverReady = true;
						else
							receiverReady = false;
					}
					else if(reply2.getSender() == witness) {
						if(reply2.getMessage() == String.valueOf(MessageCodes.READY))
							witnessReady = true;
						else
							witnessReady = false;
					}
					if(receiverReady && witnessReady) {
						// both receiver and witness are ready, go ahead
						// send commit request
						Request commitMessageReceiver = TwoPhaseProtocol.getCommitMessage(committer, receiver);
						Request commitMessageWitness = TwoPhaseProtocol.getCommitMessage(committer, witness);
						start = timeCounter.getTime();
						receiverNode.putRequest(commitMessageReceiver);
						witnessNode.putRequest(commitMessageWitness);
						reply1 = mailBox.poll(timeout, TimeUnit.MILLISECONDS);
						if(reply1 == null) {
							// take the required action
						}
						else {
							timeout -= (timeCounter.getTime() - start);
							reply2 = mailBox.poll(timeout, TimeUnit.MILLISECONDS);
							if(reply2 == null) {
								// inform the committer thread
								// ack from 1 party received 
							}
							else {
								if(reply1.getMessage() == String.valueOf(MessageCodes.ACK_COMMIT) && reply2.getMessage() == String.valueOf(MessageCodes.ACK_COMMIT)) {
									committerNode.putRequest(TwoPhaseProtocol.getInformParentThreadMessage(true, committer));
									System.out.println("Transaction can be committed");
								}
							}
						}
						
						
					}
					else {
						if(!receiverReady)
							System.out.println("Aborting the transaction, because receiver is not ready");
						if(!witnessReady)
							System.out.println("Aborting the transaction, because witness is not ready");
						// send abort message to both the sender and receiver
					}
				}	
			}
				
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putRequest(Request request) {
		try {
			mailBox.put(request);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}*/
