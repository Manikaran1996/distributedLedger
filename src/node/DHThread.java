package node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class DHThread extends Thread {
	DHT d;
	String nodeName;
	Socket sock;
	Request req;
	public DHThread(String name,String nodeName,DHT dht,Socket s,Request r) {
		// TODO Auto-generated constructor stub
		super(name);
		this.nodeName=nodeName;
		d=dht;
		sock=s;
		req=r;
		start();
	}
	public void run() {
		try {
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(sock.getOutputStream());
			String resString=d.dHTableHashMap.get(req.getMessage());
			Request request=new Request();
			request.setRecipient(req.getSender());
			request.setSender(nodeName);
			request.setRequestCode(Request.RequestCodes.SEARCH_REPLY);
			request.setMessage(resString);
			objectOutputStream.writeObject(request);
			sock.close();
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}

