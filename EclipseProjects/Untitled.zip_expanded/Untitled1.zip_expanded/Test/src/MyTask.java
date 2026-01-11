import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class MyTask implements Runnable {
	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			MyData.i++;
		}
/*		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		System.out.println("Reaching here..");*/
	}
}
