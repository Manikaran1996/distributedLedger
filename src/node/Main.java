package node;

import java.security.PrivateKey;
import java.util.Scanner;

import transaction.Transaction;
import transaction.TransactionManager;

public class Main {
	
	private static String[] pubKeys = new String[] { "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdmwPuuJOGEvgJ0kiUWAyhE2o1YfON1lnkmy+oMwom5XmKqeur3A9HvfJmc/wtYYQAal72sN/ORFrxZ9fp1fOUQMzYcQ03nWiDHHZJjZ010Rvq8t1dq1wJk3AXIgTMYmnUXZVlI7k37w9uzpiCAC6Dha8Ea9XeoiRMP7Y7KJIVXQIDAQAB",
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCYNP4ZpfG45x33zhemA86MEZGOd6OeYevlaYfUlBTp2y9LUcKWlimSDOGSPqXicxiHSxLenhTiI8WTZDpQAYx4ZDBFFRtHJSSCEAflrl4qvZqR5WXysCwr/APZhV+pXa1ja7Y/kAicMhMwXW6lcU0Ew2j4Ynjuubd4wQrQBy7lQIDAQAB",
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGKvI1bZ0cIPEnKUG9nmv6T3xIrSklbeAq7zjdjViYB59EKXs1U9IlqIQD+YGqx/psvyt8gaApbGQ1AM9Itct09K+avRjNhZuhvqyD8EHrdjQtehIFdX/PiybR/8mlgCyIyQyqB8DtU5z8cGntyImGMQ8xkNXI9u2sgp2gauwrswIDAQAB",
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIeHgMXysmgxQEedNs9BsdV3KM4qcEon3Dtc+VrzjOAl0tj9tm6ndCChwZw8ulfH7rstkKVsGDHXHWBCqKdRv4kViYihyJIgp4BlhnoV2Awgn4GQCMVZZXdPQvEuErbi/RIdmCwW2jC2L5TQThjUW70sBNTXorTXSPIWm3mC5gGwIDAQAB" };
	public static int txnId;
	public static void main(String[] args) {
		final int id = Integer.parseInt(args[0]);
		final int numOfNodes = 4;
		PrivateKey pk = null;
		txnId = 1;
		TransactionManager.initialize();
		for(int i=1;i<=numOfNodes;i++) {
			TransactionManager.addTransaction(Transaction.getDummyTransaction(pubKeys[i-1], 100.0, txnId , String.valueOf(i)));
			txnId++;
		}
		new NodeThread("Listener" + id, id);
		Scanner sc = new Scanner(System.in);
		int choice =1;
		while(choice != 5) {
			System.out.println("1. Send Bitcoins\n2. Query public key\n3. Display the hash Code of Txn. list");
			choice = sc.nextInt();
			switch(choice) {
			case 1: {
				System.out.println("Enter the receiver's account number : ");
				int rec = sc.nextInt();
				System.out.println("Enter the account number of witness : ");
				int wit = sc.nextInt();
				System.out.println("Enter the amount to be sent : ");
				double amt = sc.nextDouble();
				//DHT dht = new DHT();
				Transaction txn = TransactionManager.createTransaction(id, txnId, String.valueOf(id), 
						String.valueOf(rec), String.valueOf(wit),pubKeys[id], pubKeys[rec], amt, pk);
				new RequestTransaction("transactionManager", txn);
			}
				break;
			case 2:
				//DHT handle
				break;
			case 3:
				System.out.println(TransactionManager.getHashCode());
				break;
			}
		}
		sc.close();
	}
}
