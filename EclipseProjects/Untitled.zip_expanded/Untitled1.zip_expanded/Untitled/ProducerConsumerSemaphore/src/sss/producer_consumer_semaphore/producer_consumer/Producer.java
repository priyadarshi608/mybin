package sss.producer_consumer_semaphore.producer_consumer;

import sss.producer_consumer_semaphore.semaphore.Semaphore;
//import java.util.concurrent.Semaphore;

/**
* Producer Class.
*/
public class Producer implements Runnable{
   
   Semaphore semaphoreProducer;
   Semaphore semaphoreConsumer;
   
   public Producer(Semaphore semaphoreProducer,Semaphore semaphoreConsumer) {
          this.semaphoreProducer=semaphoreProducer;
          this.semaphoreConsumer=semaphoreConsumer;
   }

   public void run() {
          for(int i=1;i<=5;i++){
                 try {
                     semaphoreProducer.acquire();
                     System.out.println("Produced : "+i);
                     semaphoreConsumer.release();
                       
                 } catch (InterruptedException e) {
                       e.printStackTrace();
                 }
          }          
   }
}
