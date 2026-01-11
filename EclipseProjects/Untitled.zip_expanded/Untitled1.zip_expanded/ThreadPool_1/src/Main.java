import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final int NTHREDS = 10;

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
    for (int i = 0; i < 500; i++) {
      Runnable task = new MyRunnable(10000000L + i);
      executor.execute(task);
    }
    // This will make the executor accept no new threads
    // and finish all existing threads in the queue
    executor.shutdown();
    // Wait until all threads are finish
    try {
		executor.awaitTermination(1, TimeUnit.NANOSECONDS);
	} catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("Finished all threads");
  }
}
