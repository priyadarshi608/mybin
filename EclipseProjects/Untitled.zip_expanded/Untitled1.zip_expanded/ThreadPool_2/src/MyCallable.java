import java.util.concurrent.Callable;

public class MyCallable implements Callable<Long> {
	  private final long countUntil;

	  MyCallable(long countUntil) {
	    this.countUntil = countUntil;
	  }

	  @Override
	  public Long call() {
	    long sum = 0;
	    for (long i = 1; i < countUntil; i++) {
	      sum += i;
	    }
	    return sum;
	  }
} 
