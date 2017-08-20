package node;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DHT{
	private String nodeName;
	private String ordList[];
	private int n;
	private HashMap<String,String> nodeMap;
	public HashMap<String,String> dHTableHashMap;
	private int dHTcurr;
	Security security=new Security();
	public void buildDHT() {
		n= nodeMap.size();
		Set<String> namesSet=nodeMap.keySet(); 
		ordList=new String[n];
		int ind=0;
		for (Iterator<String> iterator = namesSet.iterator(); iterator.hasNext();) {
			ordList[ind++]=(String) iterator.next();
			
		}
		Arrays.sort(ordList,new Comparator<String>() {
			public int compare(String i,String j) {
				BigInteger bi=new BigInteger(Security.bytesToString(security.getHash(i)),16);
				BigInteger bj=new BigInteger(Security.bytesToString(security.getHash(j)),16);
				return bi.compareTo(bj);
			}
		});
		for (int i = 0; i < ordList.length; i++) {
			if(ordList[i].equals(nodeName)) {
				dHTcurr=i;
				
			}
		}
	}
	public DHT(String nodeName,HashMap<String, String> hp) {
		dHTableHashMap=new HashMap<String,String>();
		nodeMap=hp;
		this.nodeName=nodeName;
		buildDHT();
		}
	private String findNode(String key) {
		int currNode=dHTcurr;
		BigInteger keyValHash=new BigInteger(Security.bytesToString(security.getHash(key)),16);
		BigInteger aBigInteger=new BigInteger(Security.bytesToString(security.getHash(ordList[currNode])),16);
		BigInteger bBigInteger=new BigInteger(Security.bytesToString(security.getHash(ordList[(currNode+1)%n])),16);
		do{
			if(keyValHash.compareTo(aBigInteger)==0) return ordList[currNode];
			if(aBigInteger.compareTo(bBigInteger)<0 && keyValHash.compareTo(bBigInteger)<0 && aBigInteger.compareTo(keyValHash)<0) return ordList[(currNode+1)%n];
			if(aBigInteger.compareTo(bBigInteger)>0 && (keyValHash.compareTo(aBigInteger)>0 || (keyValHash.compareTo(aBigInteger)<0 && keyValHash.compareTo(bBigInteger)<0))) return ordList[(currNode+1)%n];
			currNode=(currNode+1)%n;
			aBigInteger=new BigInteger(Security.bytesToString(security.getHash(ordList[currNode])),16);
			bBigInteger=new BigInteger(Security.bytesToString(security.getHash(ordList[(currNode+1)%n])),16);
		}while(true);
	}
	public void putValue(String key,String value) {
		dHTableHashMap.put(key, value);
	}
	public String getValue(String key){ 
		String node=findNode(key);
		String resString="";
		try {
			Socket socket=new Socket(nodeMap.get(node),6666);
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
			Request request=new Request();
			request.setRecipient(node);
			request.setSender(nodeName);
			request.setRequestCode(Request.RequestCodes.SEARCH);
			request.setMessage(key);
			objectOutputStream.writeObject(request);
			Request recvRequest;
			ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());			
			recvRequest=(Request) objectInputStream.readObject();
			resString= recvRequest.getMessage();
			socket.close();
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resString;
	}
}
