public class Task implements Runnable {
	ThreadSafeArrayList arrayList;

	public Task(ThreadSafeArrayList arrayList) {
		this.arrayList = arrayList;
	}
	
	@Override
	public void run() {
		while (ThreadSafeArrayList.i < 6) {
			arrayList.set();
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
