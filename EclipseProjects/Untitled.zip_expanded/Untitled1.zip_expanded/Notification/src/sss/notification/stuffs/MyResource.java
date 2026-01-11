package sss.notification.stuffs;

public class MyResource {
	private volatile boolean go = false;
	
    /*
     * wait and notify can only be called from synchronized method or bock
     */
    public synchronized void shouldGo() throws InterruptedException {
    	
    	// *************** MUST READ THIS**************************************
    	// THE OTHER THREADS THAN ONE AGAIN WAITS IN CASE OF NOTIFYALL() DUE TO 
    	// THIS LOOP. IF THIS LOOP WERE NOT PRESENT, ALL THE THREADS WOULD HAVE
    	// COMPLETED THE EXECUTION AFTER NOTIFYALL().
//        if (go != true){
        while(go != true){
            System.out.println(Thread.currentThread().getName() 
                         + " is going to wait on this object");
            
            // IDEALLY wait() SHOULD BE LAST STATEMENT IN SUCH LOOPS. 
            wait(); //release lock and reacquires on wakeup
            System.out.println(Thread.currentThread() + " is woken up");
        }
        go = false; //resetting condition
    }

    /*
     * both shouldGo() and go() are locked on current object referenced by "this" keyword
     */
    public synchronized void go() {
        if (!go){
            System.out.println(Thread.currentThread().getName() 
            + " is going to notify all or one thread waiting on this object");

            go = true; //making condition true for waiting thread
            //notify(); // only one out of three waiting thread WT1, WT2,WT3 will woke up
            notifyAll(); // all waiting thread  WT1, WT2,WT3 will woke up

            /* This sleep just tells that now notifyThread has the lock on
             * the object and any of the other waiting threads will get the lock
             * only after the release of the lock by this thread. But one of the
             * woken threads will definitely get the lock when this thread will
             * release the lock.*/
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
