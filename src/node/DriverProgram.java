package node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

import node.Request.RequestCodes;
import transaction.Transaction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
public class DriverProgram {
	
	private HashMap<String,String> hashMap = new HashMap<String, String>();
	private LinkedList<Transaction> txnList;
	public DriverProgram() {
		txnList = new LinkedList<Transaction>();
	}
	
	public static void main(String[] args) {
		// instead of making every function static I have made an object of the class
		// and called the functions on the object
		
		DriverProgram dp = new DriverProgram();
		System.out.println(dp.hashMap);
		//creating the NodeThread using name given on command line 
		NodeThread myThread = new NodeThread(args[0]);
		//for DHT testing purpose
		Scanner s=new Scanner(System.in);
		while(s.hasNext()) {
			String node=s.next();
			System.out.print("Finding public key of "+node+ ":");
			System.out.println(myThread.dht.getValue(node));
		}
		/* change accordingly if required
		dp.initialize();
		for(int i=1;i<=5;i++) {
			NodeThread temp = dp.hashMap.get(String.valueOf(i));
			temp.copyList(dp.txnList);
			temp.start();
		}
		Request request = new Request();
		request.setRequestCode(RequestCodes.COMMIT);
		sender.putRequest(request);
		//sender.commitTransaction(); */
	}

	private void initialize() {
		int mapSize = hashMap.size();
		long txId = 1;
		for(int i=1;i<=mapSize;i++) {
			//update when changed
			//txnList.add(Transaction.getDummyTransaction(hashMap.get(String.valueOf(i)).pubKey, 100.0, txId , String.valueOf(i)));
			txId++;
			
		}
	}
	
}
