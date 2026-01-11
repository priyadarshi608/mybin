package testing;
//Demonstrate thread priorities.
class Clicker implements Runnable {
	long click = 0;
	Thread t;
	private volatile boolean running = true;
	public Clicker(int p) {
		t = new Thread(this);
		t.setPriority(p);
	}
	public void run() {
		while (running) {
			click++;
		}
	}
	public void stop() {
		running = false;
	}
	public void start() {
		t.start();
	}
}
class HiLoPri {
	public static void main(String args[]) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Clicker hi = new Clicker(Thread.NORM_PRIORITY + 4);
		Clicker lo = new Clicker(Thread.NORM_PRIORITY - 4);
		lo.start();
		hi.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Main thread interrupted.");
		}
		lo.stop();
		hi.stop();
		// Wait for child threads to terminate.
		try {
			hi.t.join();
			lo.t.join();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException caught");
		}
		System.out.println("Low-priority thread: " + lo.click);
		System.out.println("High-priority thread: " + hi.click);
	}
}
