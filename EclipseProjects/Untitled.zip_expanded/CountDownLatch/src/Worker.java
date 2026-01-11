import sss.countdownlatch.countdownlatch.CountDownLatch;

// A class to represent threads for which the main thread waits. 
class Worker extends Thread {
	private int delay; 
	private CountDownLatch latch; 

	public Worker(int delay, CountDownLatch latch, String name) {
		super(name); 
		this.delay = delay; 
		this.latch = latch; 
	} 

	@Override
	public void run() {
		try	{
			Thread.sleep(delay); 
			System.out.println(Thread.currentThread().getName() + " finished"); 
			latch.countDown(); 
		} 
		catch (InterruptedException e) {
			e.printStackTrace(); 
		} 
	} 
} 
