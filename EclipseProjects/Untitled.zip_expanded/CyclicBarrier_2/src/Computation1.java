import java.util.concurrent.BrokenBarrierException; 
import java.util.concurrent.CyclicBarrier; 

class Computation1 implements Runnable {
	public static int product = 0; 
	public void run() {
		product = 2 * 3; 
		try	{
			Tester.newBarrier.await(); 
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace(); 
		} 
	} 
} 
