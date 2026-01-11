package sss.threadpool.task;

/**
 * Task class which implements Runnable.
 */
public class Task implements Runnable{
	private String taskName;

	public Task(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskName() {
		return taskName;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName()
					+" is executing " + taskName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
