import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinDemo {
	  public static void main(String[] args) {
		    // create a random data set
		    final int[] data = new int[1000];
		    final Random random = new Random();
		    for (int i = 0; i < data.length; i++) {
		      data[i] = random.nextInt(100);
		    }
		 
		    // submit the task to the pool
		    final ForkJoinPool pool = new ForkJoinPool(3);
		    final MyForkJoin finder = new MyForkJoin(data);
		    System.out.println(pool.invoke(finder));
		    pool.shutdown();
		  }
}
