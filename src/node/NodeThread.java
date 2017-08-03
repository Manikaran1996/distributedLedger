package node;

public class NodeThread extends Thread {
	
	
	public NodeThread(String threadName) {
		super(threadName);
		start();
	}
	
	public void run() {
		try {
			synchronized(DriverProgram.requestQueue) {
				DriverProgram.requestQueue.wait();
				Request r = DriverProgram.requestQueue.peek();
				if(r.getRequestCode() == -1) {
					System.out.println("hello I am a thread and my name is " + getName());
				}
			}
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
