import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * @author SONU
 *
 */
public class Producer extends Thread {
	private final BlockingQueue<Integer> sharedQueue;
	private static final Random rnd = new Random();
	
	/**
	 * Max value
	 */
	public static final int MAX = 5000;
	private Producer(BlockingQueue<Integer> queueIn) {
		sharedQueue = queueIn;
	}
	
	/**
	 * @param queueIn The blocking Queue
	 * @return Producer instance
	 */
	public static Producer getInstance(BlockingQueue<Integer> queueIn) {
		return new Producer(queueIn);
	}
	
	public void run() {
//		System.out.println("This is Producer.");
		int value;
		while (true) {
			try {
				Thread.sleep(rnd.nextInt(10000)); //Pretend work is happening�
				value = rnd.nextInt(MAX);
				sharedQueue.put(value);
			} catch (InterruptedException e) {
				System.out.println("Producer interupted: " + Thread.currentThread().getName());
				Thread.currentThread().interrupt();
				return;
			}
		}
	}
}
