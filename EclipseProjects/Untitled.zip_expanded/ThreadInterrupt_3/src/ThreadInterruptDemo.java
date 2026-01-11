public class ThreadInterruptDemo {
	public static void main(String[] args) {
		System.out.println("Main thread execution STARTS"); 
		MyThread thread = new MyThread();
		thread.start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// main thread calls interrupt() method on child thread
		// TO SEE ITS IMPACT, COMMENT THE BELOW LINE
		thread.interrupt();

		System.out.println("Main thread execution COMPLETES"); 
	} 
} 
