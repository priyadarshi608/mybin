public class Task implements Runnable {
	ThreadSafeArrayList arrayList;

	public Task(ThreadSafeArrayList arrayList) {
		this.arrayList = arrayList;
	}
	
	@Override
	public void run() {
		for (int i = 1; i <= 5; i++) {
			arrayList.set(i);
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
