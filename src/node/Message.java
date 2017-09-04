package node;

import java.io.Serializable;

import transaction.Transaction;

public class Message implements Comparable<Message>,Serializable{
	int messNumber;
	int sender;
	long timestamp;
	int replyCnt;
	enum TYPE { ABCAST_REQ,ABCAST_REPLY,ABCAST_CREATE,ABCAST_ACK};
	TYPE type;
	boolean deliverable;
	Transaction transaction;
	public Message(int mNum,int sender,long times,TYPE t,int reply,Transaction tr) {
		messNumber=mNum;
		this.sender=sender;
		type=t;
		timestamp=times;
		replyCnt=reply;
		deliverable=false;
		transaction=tr;
	}
	public String toString() {
		return transaction.toString();
	}
	public int compareTo(Message m){
		if(timestamp==m.timestamp) return Integer.compare(sender, m.sender);
		return Long.compare(timestamp, m.timestamp);
	}
}
