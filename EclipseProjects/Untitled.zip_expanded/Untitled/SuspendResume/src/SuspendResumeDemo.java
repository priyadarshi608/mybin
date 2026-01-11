public class SuspendResumeDemo {
	public static void main(String args[]) {
		Worker worker1 = new Worker("task1-Worker1");
		Worker worker2 = new Worker("task1-Worker2");
		
		Worker worker3 = new Worker("task1-Worker3");
		
		worker1.startTask();
		worker2.startTask();
		worker3.startTask();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		worker1.suspendTask();
		worker2.suspendTask();
		worker3.suspendTask();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		worker1.resumeTask();
		worker2.resumeTask();
		worker3.resumeTask();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		worker1.stopTask();
		worker2.stopTask();
		worker3.stopTask();
		
		System.out.println("Exiting main");
	}
}
