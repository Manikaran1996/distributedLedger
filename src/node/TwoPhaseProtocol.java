package node;

import node.Request.RequestCodes;

public class TwoPhaseProtocol {

	public String message;
	enum MessageCodes{DONE, PREPARE, NOT_READY, READY, COMMIT, ABORT, ACK_COMMIT, ACK_NOT_COMMIT};
	
	private static Request prepareRequest(String from, String to, String message) {
		Request request = new Request();
		request.setRecipient(to);
		request.setMessage(message);
		request.setRequestCode(RequestCodes.TWO_PHASE);
		request.setSender(from);
		return request;
	}
	
	public static Request getNotReadyMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.NOT_READY));
	}
	
	public static Request getDoneMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.DONE));
	}
	
	public static Request getPrepareMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.PREPARE));
	}
	
	public static Request getReadyMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.READY));
	}
	
	public static Request getCommitMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.COMMIT));
	}
	
	public static Request getAbortMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.ABORT));
	}
	
	public static Request getCommitAckMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.ACK_COMMIT));
	}
	
	public static Request getNotCommitAckMessage(String from, String to) {
		return prepareRequest(from, to, String.valueOf(MessageCodes.ACK_NOT_COMMIT));
	}
	
	
	public static void main(String[] args) {
		Request r = getPrepareMessage("A", "B");
		System.out.println(r.getMessage());
	}
	
}
