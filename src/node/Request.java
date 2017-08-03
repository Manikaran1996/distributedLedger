package node;

import java.util.Scanner;

public class Request {
	private String to;
	private int requestCode;
	/* 
	 * requestCode			Remark
	 * 		1			Search the public key
	 * 		2			Verify the transaction
	 * 		3 			2 Phase protocol message 		
	 */
	private String message;
	private Transaction txn;
	private String name;
	
	public void createTransactionRequest(Transaction txn) {
		Scanner sc = new Scanner(System.in);
		System.out.print("To : ");
		to = sc.next();
		this.txn = txn;
		requestCode = 2;
	}
	
	public void createSearchKeyRequest() {
		requestCode = 1;
	
	}
	public void createProtocolMessageRequest() {
		requestCode = 3;
	}
	
	public void genericRequest() {
		requestCode = -1;
	}
	
	public int getRequestCode() {
		return requestCode;
	}
}
