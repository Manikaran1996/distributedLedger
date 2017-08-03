package node;

import java.util.LinkedList;
import java.util.Queue;

public class DriverProgram {
	
	public static Queue<Request> requestQueue = new LinkedList<Request>();
	
	public static void main(String[] args) {

		new DriverProgram().createARequest();
	}
	
	public void createARequest() {

		new NodeThread("Thread 1");
		new NodeThread("Thread 2");
		new NodeThread("Thread 3");
		new NodeThread("Thread 4");
		
		Request myRequest = new Request();
		myRequest.genericRequest();
		requestQueue.add(myRequest);
		
		synchronized(requestQueue) {
			requestQueue.notifyAll();
		}
	}
}
