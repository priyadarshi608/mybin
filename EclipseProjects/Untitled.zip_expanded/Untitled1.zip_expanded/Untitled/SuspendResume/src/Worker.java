public class Worker extends Thread {

	private volatile boolean suspendTask = false;
	private volatile boolean stopTask = false;

	public Worker(String threadName) {
		super(threadName);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { stopTask(); }
		});
	}
	
	@Override
	public void run() {
		while (!stopTask) {
			System.out.println(Thread.currentThread() + " running..");
			while (suspendTask) {
				System.out.println("Going to wait on " +
						Thread.currentThread().getName() + " as this thread have"
						+ " to get suspended.");
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException("InterruptedException in the suspended"
							+ " state of the Task. The stack traces is: " + e);
				}
				System.out.println(Thread.currentThread() + " resumed..");
				System.out.println(Thread.currentThread() + " doing heavy task..");
			}
		}
		System.out.println(Thread.currentThread() + " terminated..");
	}

	public void startTask() {
		start();
	}
	
	public void suspendTask() {
		this.suspendTask = true;
	}

	synchronized public void resumeTask() {
		this.suspendTask = false;
		notify();
	}

	public void stopTask() {
		this.stopTask = true;
	}
}
