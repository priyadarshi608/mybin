import java.text.SimpleDateFormat;
import java.util.Random;

public class MyThreadLocalTask implements Runnable{

	// SimpleDateFormat is not thread-safe, so give one to each thread
	private static final ThreadLocal<SimpleDateFormat> formatter = 
			new ThreadLocal<SimpleDateFormat>() {
		
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd HHmm");
		}
	};

	@Override
	public void run() {
		System.out.println("Thread: "+Thread.currentThread().getName()+" default Formatter = "+formatter.get().toPattern());
		
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		formatter.set(new SimpleDateFormat());

		System.out.println("Thread: "+Thread.currentThread().getName()+" formatter = "+formatter.get().toPattern());
	}
}
