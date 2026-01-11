import sss.notification.stuffs.MyResource;
import sss.notification.tasks.NotifyTask;
import sss.notification.tasks.WaitTask;

public class NotificationDemo {

	public static void main(String[] args) {
		MyResource myResource = new MyResource();
		WaitTask waitTask = new WaitTask(myResource);
		NotifyTask notifyTask = new NotifyTask(myResource);
		
		Thread waitThread1 = new Thread(waitTask, "waitThread 1");
		Thread waitThread2 = new Thread(waitTask, "waitThread 2");
		Thread waitThread3 = new Thread(waitTask, "waitThread 3");
		
		Thread notifyThread = new Thread(notifyTask, "notifyThread..");
		
		waitThread1.start();
		waitThread2.start();
		waitThread3.start();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		notifyThread.start();
	}
}
