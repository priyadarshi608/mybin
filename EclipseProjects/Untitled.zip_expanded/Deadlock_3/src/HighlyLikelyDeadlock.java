public class HighlyLikelyDeadlock {
	static class Locker implements Runnable {
		private Object first, second;

		Locker(Object first, Object second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public void run() {
			while (true) {
				synchronized (first) {
					System.out.println("inside first.");
					synchronized (second) {
						System.out.println("inside second.");
						System.out.println(Thread.currentThread().getName());
					}
				}
			}
		}
	}

	public static void main(final String... args) {
		Object lock1 = new Object(), lock2 = new Object();
		new Thread(new Locker(lock1, lock2), "Thread 1").start();
		new Thread(new Locker(lock2, lock1), "Thread 2").start();
	}
}
