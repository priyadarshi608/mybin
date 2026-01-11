import sss.deadlock_3.resource.Resource1;
import sss.deadlock_3.resource.Resource2;

public class DeadlockDemo implements Runnable {
	Resource1 a = new Resource1();
	Resource2 b = new Resource2();
	
	DeadlockDemo() {
		Thread.currentThread().setName("MainThread");
		Thread t = new Thread(this, "RacingThread");
		t.start();
		a.first(b); // get lock on a in this thread.
		System.out.println("Back in main thread");
	}
	public void run() {
		b.first(a); // get lock on b in other thread.
		System.out.println("Back in other thread");
	}
	public static void main(String args[]) {
		new DeadlockDemo();
	}
}
