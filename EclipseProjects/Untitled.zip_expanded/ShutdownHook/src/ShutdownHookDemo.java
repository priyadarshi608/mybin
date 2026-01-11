import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ShutdownHookDemo {

	// a class that extends thread that is to be called when program is exiting
	static class Message extends Thread {

		public void run() {
			System.out.println("Run method of message class called.");
			System.out.println("Bye.");
		}
	}

	public static void main(String[] args) {

		System.out.println("Main thread name: " + Thread.currentThread().getName());
		/*
		 * setDefaultUncaughtExceptionHandler method sets the default handler
		 * which is called when a thread terminates due to an
		 * uncaught unchecked(runtime) exception generated in run() method.
		 */
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			public void uncaughtException(Thread thread, Throwable throwable) {
				System.out.println(thread.getName() + " has thrown " + throwable);
			}
		});
		
//		int j = 100 / 0;
		
		try {

			// register Message as shutdown hook
			Runtime.getRuntime().addShutdownHook(new Message());

			// print the state of the program
			System.out.println("Program is starting...");

			// cause thread to sleep for 3 seconds
			System.out.println("Waiting for 3 seconds...");
			Thread.sleep(3000);

			int i = 100 / 0;
			
			// print that the program is closing 
			System.out.println("Program is closing...");


//		} catch (InterruptedException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
