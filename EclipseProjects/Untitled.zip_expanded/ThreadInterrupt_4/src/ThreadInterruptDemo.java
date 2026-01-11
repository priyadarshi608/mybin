// Does calling Thread.interrupt() before a Thread.join() cause the join() to throw an InterruptedException immediately?
// Only if the current thread that is calling the join() method gets interrupted will join() throw InterruptedException.
public class ThreadInterruptDemo {
	
	public static void main(String[] args) {
		System.out.println("Main thread execution STARTS"); 
		MyThread thread = new MyThread();
		thread.start();

		// TO SEE ITS IMPACT, COMMENT THE BELOW LINE
		Thread.currentThread().interrupt();
		System.out.println("After calling currentThread.interrupt");
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Main thread execution COMPLETES"); 
	} 
} 
