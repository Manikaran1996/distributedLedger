package node;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;

import transaction.Transaction;

public class Request implements Serializable{
	enum RequestCodes {SEARCH, VERIFY, TWO_PHASE,  ADD_KEY,SEARCH_REPLY, DEFAULT, TWO_PHASE_REPLY, COMMIT,TRANSACTION,INIT,INIT_REPLY,PING,FAILURE};
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
	private List<Transaction> tl;
	private String sender;
	
	public void setTransactionBackup(List<Transaction> l) {
		tl=l;
	}
	public List<Transaction> getTransactionBackup() {
		return tl;
	}
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
		builder.append("From : ");
		builder.append(sender);
		builder.append("Request Code : ");
		builder.append(requestCode);
		builder.append("Message : ");
		builder.append(message);
		return builder.toString();
	}
}
