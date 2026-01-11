package testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* Logger Interface
*
* @author sss
*/

//public interface Log 
//{
//    void log(String fileName, String message);
//}
public class ApiUser {

	public static void main(String[] args) {
		LogImpl loggerA = new LogImpl("ApiUser", "C:\\Users\\sonu\\Desktop\\files\\a.txt");
		LogImpl loggerB = new LogImpl("ApiUser", "C:\\Users\\sonu\\Desktop\\files\\b.txt");
		loggerA.log("C:\\Users\\sonu\\Desktop\\files\\a.txt", "Abc");
		loggerA.log("C:\\Users\\sonu\\Desktop\\files\\a.txt", "Ade");
		loggerB.log("C:\\Users\\sonu\\Desktop\\files\\b.txt", "aBc");
		loggerA = null;
		loggerB = null;
		System.gc();
	}
}
/**
* Class members start with 'my'.
* 
* @author sss
*/

class LogImpl //implements Log
{
    // constant members
	private static final String CLASS_NAME_STR = "ClassName: ";
	private static final String TIME_STR       = "Time: ";
	private static final String MESSAGE_STR    = "Message: ";
	
	private final String                   myClassName;
	private final AsynchronousLoggingQueue myQueue;
	
	public LogImpl(String className, String fileName){
	    myClassName = className;
		myQueue     = AsynchronousLoggingQueue.getInstance(fileName);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {myQueue.stopLogger();}
		});
	}
	
	/*(non-javadoc)
	* @see sss.logger.api.Log#log(java.lang.String, java.lang.String)
	*/
	//@Override
	public void log(String fileName, String message) {
	    // Concatenates the log message with current time and the name of the 
		// calling class
		if (fileName != null && !fileName.isEmpty()) {
		    if (message != null && !message.isEmpty()) {
			    String fullMessage = CLASS_NAME_STR + myClassName + TIME_STR +
				                     System.currentTimeMillis() + MESSAGE_STR +
									 message;
				myQueue.writeMessage(fullMessage);
			}
		}
	}
}
/**
* Queue which keeps track of all the messages to write. It writes each of the
* message asynchronously. Blocking occurs only when two or more threads write to
* the same file, because in that case these threads will share the same queue.
*
* @author sss
*/

class AsynchronousLoggingQueue extends Thread 
{
    private static final Map<String, AsynchronousLoggingQueue> myFileNameToQueue =
	    new HashMap<String, AsynchronousLoggingQueue> ();
		
	private final ConcurrentLinkedQueue<String> myQueue = new ConcurrentLinkedQueue<String> ();
	
	//File name for which queue is serving.
	private final String myFileName;
	
	private volatile boolean myShouldStop = false;
	
	/**
	* Constructor
	*/
	public AsynchronousLoggingQueue (String fileName) {
	    myFileName = fileName;
	}

	public static synchronized AsynchronousLoggingQueue getInstance(String fileName) {
	    if(myFileNameToQueue.containsKey(fileName)) {
		    return myFileNameToQueue.get(fileName);
		}
		AsynchronousLoggingQueue queue = new AsynchronousLoggingQueue(fileName);
		myFileNameToQueue.put(fileName, queue);
		queue.start();
		// Asynchronously start writing the data of the queue.
		return queue;
	}
	
	public void run() {
		FileWriter fw     = null;
	    BufferedWriter bw = null;
		try {
			fw = new FileWriter(myFileName);
		    bw = new BufferedWriter(fw);
			while (!myShouldStop || !myQueue.isEmpty()) {
			    String next = myQueue.poll();
			    if (myQueue.isEmpty())
			    System.out.println("It's running.");
				if (next != null) {
				    try {
					    bw.write(next);
			    	    bw.flush();
					}
					catch (IOException e) {
					    throw new RuntimeException("IOException while trying to write into the file" +
						                           myFileName + ". The IOException Stack Trace is" + 
												   e);
					}
				}
			}
		}
		catch (IOException e) {
		    throw new RuntimeException("IOException while trying to open the file " + 
			                           myFileName + " for writing. The IOException Stack Trace "
									   + "is" + e);
		}
		finally {
    		try {
	    	    bw.flush();
	    		bw.close();
		    }
    		catch (IOException e) {
	    	    throw new RuntimeException("IOException while trying to close the file" + 
	    		                           myFileName + ". The IOException Stack Trace is " + e);
		   }
		}
	}
		
	/*
	* Stop the logging service. It is wise to be called by the shutdown hook.
	*/
	public void stopLogger () {
	    myShouldStop = true;
	}
	
	/*
	* Adds the message to the queue, so that it will be drained later by separate thread.
	*/
	public void writeMessage (String message) {
	    myQueue.add(message);
	}
}
