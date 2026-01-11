package sss.asynch_logger.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsynchFileLogger extends Thread implements Logger {

	// constants to reduce some garbage.
	private static final String CLASS_NAME_STR = "ClassName: ";
	private static final String TIME_STR       = ", Time: ";
	private static final String MESSAGE_STR    = ", Message: ";

	private static final Map<String, AsynchFileLogger> LoggerMap = 
			new HashMap<String, AsynchFileLogger>();

	private ConcurrentLinkedQueue<String> myQueue = new ConcurrentLinkedQueue<>();

	private final String myClassName;
	private final String myFileName;

	private boolean suspendLogger = false;
	private boolean stopLogger = false;

	synchronized public static AsynchFileLogger getLogger(
			// V V I - Class<?> className
			Class<?> className, String fileName) {

		if (LoggerMap.containsKey(fileName))
			return LoggerMap.get(fileName);
		else {
			AsynchFileLogger myLogger = new AsynchFileLogger(className.getName(), fileName);
			LoggerMap.put(fileName, myLogger);
			myLogger.start();
			return myLogger;
		}
	}

	public void run() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(myFileName));) {
			while (!stopLogger) {
				while (suspendLogger) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						throw new RuntimeException("InterruptedException in the"
								+ " suspended state of the logger. The stack "
								+ "traces is: " + e);
					}
				}

				String string = myQueue.poll();
				if (string != null) {
					try {
						bw.write(string);
						bw.flush();
					} catch (IOException e) {
						throw new RuntimeException("IOException while trying to"
								+ " write on the file " + myFileName + ". The "
								+ "IOException stacktrace is " + e);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException while trying to open the "
					+ "file " + myFileName + " for writing. The IOException "
					+ "stacktrace is " + e);
		}
	}

	public void stopLogger() {
		this.stopLogger = true;
	}

	public void suspendLogger() {
		this.suspendLogger = true;
	}

	synchronized public void resumeLogger() {
		this.suspendLogger = false;
		notify();
	}

	@Override
	public void logMessage(String message) {
		if (message != null && !message.isEmpty()) {
			message = CLASS_NAME_STR + myClassName + 
					TIME_STR + System.currentTimeMillis() +  
					MESSAGE_STR + message;
			myQueue.add(message);
		}
	}

	private AsynchFileLogger(String className, String fileName) {
		this.myClassName = className;
		this.myFileName = fileName;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { stopLogger(); }
		});
	}
}
