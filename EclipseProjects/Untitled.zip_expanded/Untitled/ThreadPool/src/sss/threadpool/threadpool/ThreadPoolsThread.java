package sss.threadpool.threadpool;

import sss.threadpool.myblockingqueue.BlockingQueueCustom;

/**
* These threads are created and started from constructor of ThreadPool class.
*/
class ThreadPoolsThread extends Thread {

   private BlockingQueueCustom<Runnable> taskQueue;
   private ThreadPool threadPool;

   public ThreadPoolsThread(BlockingQueueCustom<Runnable> queue,
                 ThreadPool threadPool){
       taskQueue = queue;
       this.threadPool=threadPool;
      
   }

   public void run() {
          try {
                 /*
                  * ThreadPool's threads will keep on running
                  * until ThreadPool is not shutDown (shutDown will interrupt thread) and
                  * taskQueue contains some unExecuted tasks.
                  */
                 while (true) {    
                       System.out.println(Thread.currentThread().getName()
                                     +" is READY to execute task.");
                       /*ThreadPool's thread will take() task from sharedQueue
                        * only if tasks are available else
                        * waits for tasks to become available.
                        */
                       Runnable runnable = taskQueue.take();
                       System.out.println(Thread.currentThread().getName()
                                     +" has taken task.");
                       //Now, execute task with current thread.
                       runnable.run();                
                       
                       System.out.println(Thread.currentThread().getName()
                                     +" has EXECUTED task.");
                       
                       /*
                        * 1) Check whether pool shutDown has been initiated or not,
                        * if pool shutDown has been initiated and
                        * 2) taskQueue does not contain any
                        *    unExecuted task (i.e. taskQueue's size is 0 )
                        * than  interrupt() the thread.
                        */
                       if(this.threadPool.isPoolShutDownInitiated()
                                     &&  this.taskQueue.size()==0){
                              this.interrupt();
                              /*
                               *  Interrupting basically sends a message to the thread
                               *  indicating it has been interrupted but it doesn't cause
                               *  a thread to stop immediately,
                               * 
                               *  if sleep is called, thread immediately throws
                               *  InterruptedException
                               */
                              Thread.sleep(1);  
                       }   
                 }
          } catch (Exception e) {
                 System.out.println(Thread.currentThread().getName()+" has been STOPPED.");
          }
   }
}
