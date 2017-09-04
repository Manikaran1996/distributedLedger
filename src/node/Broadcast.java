package node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import transaction.Transaction;
import transaction.TransactionManager;

import node.Request.RequestCodes;

public class Broadcast extends Thread {
	public static HashMap<String, String> nodeMap;
	static final int PORT=4000;
	static final int PACK_SIZE=1000000;
	DatagramSocket mailBox;
	String nodeName;
	DHT dht;
	int cnt=0;
	long mcount=0;
	NodeThread nt;
	boolean receivedTranList=false;
	ArrayList<Message> queue=new ArrayList<Message>();
	public Broadcast(String tname,HashMap<String, String> mp,DHT d,String nname,NodeThread t) {
		super(tname);
		nodeMap=mp;
		nt=t;
		try {
			mailBox=new DatagramSocket(PORT);
			mailBox.setBroadcast(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dht=d;
		nodeName=nname;
		start();
	}
	public void run() {
		byte[] inComingData=new byte[PACK_SIZE];
		while(true) {
		try {
			while(queue.size()!=0) {	
				Message m=queue.get(0);
				if(!nodeMap.containsKey(String.valueOf(m.sender))) {
					queue.remove(0);
					continue;
				}
				if(m.deliverable) {
						m.transaction.setTransactionId(++TransactionManager.maxTxnId);
						//System.out.println("Timestamp : "+m.transaction.getTransactionId());
						if(TransactionManager.verifyTransaction(m.transaction))	TransactionManager.addTransaction(m.transaction);
						else System.out.println("/* transaction "+m.transaction.getSender()+" -> " + m.transaction.getReceiver()+ " amt = " +m.transaction.getAmount()+ " is invalid */");
						queue.remove(0);
						//System.out.println("Added transaction :\n"+m.transaction);
					}
				else break;
			}
			DatagramPacket incomDatagramPacket=new DatagramPacket(inComingData, inComingData.length);
			mailBox.receive(incomDatagramPacket);
			//System.out.println("Message received");
			byte[] data= incomDatagramPacket.getData();
			ByteArrayInputStream in=new ByteArrayInputStream(data);
			ObjectInputStream is=new ObjectInputStream(in);
			Object object=is.readObject();
			//Request request=(Request)is.readObject();
			if(object instanceof Request) {
				Request request=(Request) object;
				if(request.getRequestCode()==RequestCodes.INIT && !request.getSender().equals(nodeName)) {
				nodeMap.put(request.getSender(),request.getMessage());
				dht.buildDHT();
				Request r=new Request();
				r.setRecipient(request.getSender());
				r.setSender(nodeName);
				r.setRequestCode(RequestCodes.INIT_REPLY);
				r.setMessage(nodeMap.get(nodeName));
				r.setTransactionBackup(TransactionManager.transactionList);
				putMessage(r, request.getSender());
				}
				else if(request.getRequestCode()==RequestCodes.INIT_REPLY) {
				nodeMap.put(request.getSender(),request.getMessage());
				dht.buildDHT();
				if(!receivedTranList) {
					mcount=TransactionManager.copyList(request.getTransactionBackup());
					receivedTranList=true;
				}
				}
				else if(request.getRequestCode()==RequestCodes.FAILURE) {
				if(nodeMap.containsKey(request.getMessage())) {
					nodeMap.remove(request.getMessage());
					dht.buildDHT();
					}
				}
			}
			else if(object instanceof Message) {
				Message message=(Message) object;
				if(message.type==Message.TYPE.ABCAST_CREATE) {
					Message m=new Message(++cnt,Integer.valueOf(nodeName),++mcount,Message.TYPE.ABCAST_REQ,nodeMap.size()-1,message.transaction);
					broadcastMessage(m);
					queue.add(m);
				}
				else if(message.type==Message.TYPE.ABCAST_REQ && !nodeName.equals(String.valueOf(message.sender))) {
					long timeS=message.timestamp;
					if(timeS<(1+mcount)) timeS=++mcount;
					
					Message m=new Message(message.messNumber,message.sender,timeS,Message.TYPE.ABCAST_REPLY,0,message.transaction);
					putMessage(m,String.valueOf(message.sender));
					queue.add(m);
				}
				else if(message.type==Message.TYPE.ABCAST_REPLY) {
					for (Iterator<Message> i = queue.iterator(); i
							.hasNext();) {
						Message t = i.next();
						if(t.messNumber==message.messNumber && t.sender==message.sender) {
							if(t.timestamp<message.timestamp) t.timestamp=message.timestamp;
							t.replyCnt--;
							if(t.replyCnt==0) {
								t.deliverable=true;
								Collections.sort(queue);
								if(mcount<t.timestamp) mcount=t.timestamp;
								t.type=Message.TYPE.ABCAST_ACK;
								broadcastMessage(t);
							}
						break;	
						}
					}
				}
				else if(message.type==Message.TYPE.ABCAST_ACK && !nodeName.equals(String.valueOf(message.sender))) {
					for (Iterator<Message> i = queue.iterator(); i
							.hasNext();) {
						Message t = i.next();
						if(t.messNumber==message.messNumber && t.sender==message.sender) {
							if(t.timestamp<message.timestamp) t.timestamp=message.timestamp;
							t.deliverable=true;
							Collections.sort(queue);
							if(mcount<t.timestamp) mcount=t.timestamp;
							break;	
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	public void putMessage(Object m,String s) {
		try {
			DatagramSocket socket=new DatagramSocket();
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(outputStream);
			os.writeObject(m);
			byte[] data=outputStream.toByteArray();
			InetAddress ipAddress=InetAddress.getByName(nodeMap.get(s));
			DatagramPacket sePacket=new DatagramPacket(data, data.length, ipAddress,PORT);
			socket.send(sePacket);
			socket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void broadcastMessage(Object m){
		try {
			DatagramSocket socket=new DatagramSocket();
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(outputStream);
			os.writeObject(m);
			byte[] data=outputStream.toByteArray();
			InetAddress ipAddress=InetAddress.getByName("10.255.255.255");
			DatagramPacket sePacket=new DatagramPacket(data, data.length, ipAddress,PORT);
			socket.send(sePacket);
			socket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void broadcastTransaction(Transaction t) { 
		Message message=new Message(0,0,0,Message.TYPE.ABCAST_CREATE,0,t);
		putMessage(message, nodeName);
	}
}
