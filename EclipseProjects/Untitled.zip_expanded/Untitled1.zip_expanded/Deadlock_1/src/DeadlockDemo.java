import java.util.logging.Level;
import java.util.logging.Logger;

import customizedlogger.CustomizedLogger;
import deallock_1.Resource1;
import deallock_1.Resource2;
import deallock_1.Task1;
import deallock_1.Task2;

public class DeadlockDemo {
	private static final Logger LOGGER = 
			CustomizedLogger.getCustomizedLogger(DeadlockDemo.class.getName());
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				LOGGER.log(Level.SEVERE, thread.getName().toString() 
						+ " has thrown " 
						+ throwable);
			}
		});

		Resource1 resource1 = new Resource1();
		Resource2 resource2 = new Resource2();

		Thread thread1 = new Thread(new Task1(resource1, resource2), "Thread1");
		Thread thread2 = new Thread(new Task2(resource1, resource2), "Thread2");

		thread1.start();
		thread2.start();
	}
}
