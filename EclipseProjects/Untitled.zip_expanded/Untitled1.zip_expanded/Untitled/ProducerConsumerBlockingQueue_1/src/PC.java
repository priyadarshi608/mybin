import java.util.concurrent.BlockingQueue;

/**
 * @author SONU
 *
 */
public class PC {

	public PC() {}

	/**
	 * @param args Command line arguments
	 * @throws InterruptedException Throws this exception
	 */
	public static void main(String[] args) throws InterruptedException {
		int NUM_OF_PRODUCERS = 100;
		//BlockingQueue<Integer> dataStream = new LinkedBlockingQueue<Integer>();
		BlockingQueue<Integer> dataStream = new MyLinkedBlockingQueue<Integer>();
		Consumer consumer = Consumer.getInstance(dataStream);
		Producer[] producers = new Producer[NUM_OF_PRODUCERS];
		for (int i=0; i<NUM_OF_PRODUCERS; i++) {
			producers[i] = Producer.getInstance(dataStream);
			producers[i].start();
		}
		System.out.println("Producers started...");
		consumer.start();
		System.out.println("Consumer started...");
		Thread.sleep(5000);
		consumer.interrupt();
		consumer.join();
		System.out.println("Consumer.sum: " + consumer.sum);
		System.out.println("Consumer.count: " + consumer.count);
		System.out.println("Average: " + consumer.sum / consumer.count);
		for (int i=0; i<NUM_OF_PRODUCERS; i++) {
			producers[i].interrupt();
		}
		for (int i=0; i<NUM_OF_PRODUCERS; i++) {
			producers[i].join();
		}
		System.out.println("Size of dataStream: " + dataStream.size());
		dataStream.forEach(System.out::println);
	}
}
