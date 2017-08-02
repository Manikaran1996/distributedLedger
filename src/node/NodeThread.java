package node;

public class NodeThread extends Thread {
	
	
	public NodeThread(String threadName) {
		super(threadName);
		start();
	}
	
	public void run() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String query(String nodeName) {
		return null;
	}
	
	public String verify(Transaction t) {
		return null;
	}
	

}
