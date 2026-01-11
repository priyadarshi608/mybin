package sss.producer_consumer_semaphore.semaphore;

/** Copyright (c), AnkitMittal JavaMadeSoEasy.com */ 
/**
* @author AnkitMittal
* Copyright (c), AnkitMittal .
* All Contents are copyrighted and must not be reproduced in any form.
* A semaphore controls access to a shared resource by using permits.
   - If permits are greater than zero, then semaphore
     allow access to shared resource.
   - If permits are zero or less than zero, then semaphore
     does not allow access to shared resource.

*/
public class Semaphore{
   
   private int permits;
   
   /** permits is the initial number of permits available.
          This value can be negative, in which case releases must occur
          before any acquires will be granted, permits is number of threads
          that can access shared resource at a time.
          If permits is 1, then only one threads that can access shared
          resource at a time.
   */
   public Semaphore(int permits) {
          this.permits=permits;
   }

   /**Acquires a permit if one is available and decrements the
      number of available permits by 1.
          If no permit is available then the current thread waits
          until one of the following things happen >
           >some other thread calls release() method on this semaphore or,
           >some other thread interrupts the current thread.
   */
   public synchronized void acquire() throws InterruptedException {
          //Acquires a permit, if permits is greater than 0 decrements
          //the number of available permits by 1.
          if(permits > 0){
                 permits--;
          }
          //permit is not available wait, when thread
          //is notified it decrements the permits by 1
          else{
                 this.wait();
                 permits--;
          }
   }

   /** Releases a permit and increases the number of available permits by 1.
          For releasing lock by calling release() method it’s not mandatory
          that thread must have acquired permit by calling acquire() method.
   */
   public synchronized void release() {
          //increases the number of available permits by 1.
          permits++;
          
          //If permits are greater than 0, notify waiting threads.
          if(permits > 0)
                 this.notifyAll();
   }
}
