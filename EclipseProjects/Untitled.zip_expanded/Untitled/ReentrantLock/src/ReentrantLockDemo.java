public class ReentrantLockDemo {

	public static void main(String[] args) {
		ThreadSafeArrayList arrayList = new ThreadSafeArrayList();
		Task task = new Task(arrayList);
		Thread thread1 = new Thread(task, "thread: 1");
		Thread thread2 = new Thread(task, "thread: 2");
		
		thread1.start();
		thread2.start();
	}

}
