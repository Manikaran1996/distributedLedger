package node;

import java.util.Scanner;

public class Request {
	enum RequestCodes {SEARCH, VERIFY, TWO_PHASE,  ADD_KEY,SEARCH_REPLY, DEFAULT, TWO_PHASE_REPLY, COMMIT};
	private String to;
	private RequestCodes requestCode;
	/* 
	 * requestCode			Remark
	 * 		1			Search the public key ( request)
	 * 		2			Verify the transaction
	 * 		3 			2 Phase protocol message
			4 			add public key 		
	 */
	private String message;
	private Transaction txn;
	private String sender;
	
	public void setRecipient(String to) {
		this.to= to;
	}
	
	public String getRecipient() {
		return to;
	}
	
	public void setMessage(String msg) {
		this.message = msg;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setRequestCode(RequestCodes code) {
		requestCode = code;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return sender;
	}
	
	public RequestCodes getRequestCode() {
		return requestCode;
	}
	
	public void createTransactionRequest(Transaction txn) {
		Scanner sc = new Scanner(System.in);
		System.out.print("To : ");
		to = sc.next();
		this.txn = txn;;
	}
	
	public void createSearchKeyRequest() {
		requestCode = RequestCodes.SEARCH;
	
	}
	public void genericRequest() {
		requestCode = RequestCodes.DEFAULT;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("To : ");
		builder.append(to);
		builder.append("Request Code : ");
		builder.append(requestCode);
		builder.append("Message : ");
		builder.append(message);
		return builder.toString();
	}
}
