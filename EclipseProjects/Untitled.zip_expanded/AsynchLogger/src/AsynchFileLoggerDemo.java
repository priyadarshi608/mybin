import java.util.logging.Level;
import java.util.logging.Logger;

import sss.asynch_logger.logger.AsynchFileLogger;
import sss.asynch_logger.my_customized_logger.CustomizedLogger;

public class AsynchFileLoggerDemo {
	private static final Logger LOGGER = 
			CustomizedLogger.getCustomizedLogger(AsynchFileLoggerDemo.class.getName());
	
	public static void main(String args[]) {
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				LOGGER.log(Level.SEVERE, thread.getName().toString() 
						+ " has thrown " 
						+ throwable);
			}
		});
		
		String file1 = "./asynch_logger_1.txt";
		AsynchFileLogger asynchLogger1 = AsynchFileLogger.getLogger(AsynchFileLoggerDemo.class, file1);
		String msg1 = "Write the 1st message to the file.";
		asynchLogger1.logMessage(msg1);

		String file2 = "./asynch_logger_2.txt";
		AsynchFileLogger asynchLogger2 = AsynchFileLogger.getLogger(AsynchFileLoggerDemo.class, file2);
		String msg2 = "Write the 2nd message to the file.";
		asynchLogger2.logMessage(msg2);
		
		asynchLogger2.suspendLogger();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		asynchLogger2.resumeLogger();
		String msg3 = "Write the 3rd message to the file.";
		asynchLogger2.logMessage(msg3);
		
		AsynchFileLogger asynchLogger3 = AsynchFileLogger.getLogger(AsynchFileLoggerDemo.class, file2);
		String msg4 = "Write the 4th message to the file.";
		asynchLogger3.logMessage(msg4);
	}
}
