import java.util.concurrent.BlockingQueue;

/**
 * @author SONU
 *
 */
public class Consumer extends Thread {
	private final BlockingQueue<Integer> sharedQueue;
	private static final int NUM_TO_USE = 5000;
	/**
	 * 
	 */
	public long sum = 0;
	/**
	 * 
	 */
	public long count = 0;
	private Consumer(BlockingQueue<Integer> queueIn) {
		sharedQueue = queueIn;
	}
	/**
	 * @param queueIn Consumer
	 * @return Consumer instance
	 */
	public static Consumer getInstance(BlockingQueue<Integer> queueIn) {
		return new Consumer(queueIn);
	}
	public void run() {
//		System.out.println("This is Consumer.");
		int newValue;
		for (int i=0; i<NUM_TO_USE; i++) {
			try {
				newValue = sharedQueue.take();
				sum+=newValue;
				count++;
			} catch (InterruptedException e) {
				System.out.println("Consumer interupted: " + Thread.currentThread().getName());
				Thread.currentThread().interrupt();
				return;
			}
		}
	}
}