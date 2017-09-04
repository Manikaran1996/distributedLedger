package node;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import node.Request.RequestCodes;

public class DHTChecker extends Thread {
 DHT dht;
 HashMap<String, String> nodeMap;
 String sN,sIP;
 Broadcast broadcast;
 public boolean stopFlag=false;
 public DHTChecker(String tname,DHT d,HashMap<String, String>hp,Broadcast b,String sucName,String sucIP) {
	 super(tname);
	 dht=d;
	 nodeMap=hp;
	 sN=sucName;
	 sIP=sucIP;
	 broadcast=b;
	 start();
 }
 public void run() {
	 while(true) {
		 try {
			 sleep(100);
			 if(stopFlag) return;
			Socket s=new Socket(sIP,NodeThread.PORT);
			Request request=new Request();
			request.setRequestCode(RequestCodes.PING);
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(s.getOutputStream());
			objectOutputStream.writeObject(request);
			s.close();
		} catch (Exception e) {
			Request r=new Request();
			r.setRequestCode(RequestCodes.FAILURE);
			r.setMessage(sN);
			broadcast.broadcastMessage(r);			
			return;
		} 
	 }
 }
}
