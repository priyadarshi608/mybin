
public class CyclicBarrierDemo {
	public static void main(String[] args) {
		/**
		 * Create CountDownLatch with 3 parties, when all 3 parties
		 * will reach common barrier point CyclicBarrrierEvent will be
		 * triggered i.e. run() method of CyclicBarrrierEvent will be called
		 */
		CyclicBarrier cyclicBarrier=new CyclicBarrier(3 ,new CyclicBarrierEvent());
		System.out.println("CountDownLatch has been created with parties=3,"
				+ " when all 3 parties will reach common barrier point "
				+ "CyclicBarrrierEvent will be triggered");

		MyTask myRunnable1=new MyTask(cyclicBarrier);

		//Create and start 3 threads
		new Thread(myRunnable1,"Thread-1").start();
		new Thread(myRunnable1,"Thread-2").start();
		new Thread(myRunnable1,"Thread-3").start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Create and start 3 more threads
		new Thread(myRunnable1,"Thread-4").start();
		new Thread(myRunnable1,"Thread-5").start();
		new Thread(myRunnable1,"Thread-6").start();
	}
}
