package node;

import java.util.LinkedList;

public class Transaction {
	private long txnId;
	private String digitalSignature, receiver, sender, witness;
	private double amount;
	private LinkedList<Transaction> input;
}
