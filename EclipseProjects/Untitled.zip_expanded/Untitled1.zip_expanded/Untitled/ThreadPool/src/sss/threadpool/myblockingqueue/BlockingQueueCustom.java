package sss.threadpool.myblockingqueue;

/**
* Implementing custom BlockingQueue interface .
* This BlockingQueue implementation follows FIFO (first-in-first-out).
* New elements are inserted at the tail of the queue,
* and removal elements is done at the head of the queue.
*
* @author AnkitMittal
* Copyright (c), AnkitMittal .
* All Contents are copyrighted and must not be reproduced in any form.
*/
public interface BlockingQueueCustom<E> {

     /**
      * Inserts the specified element into this queue
      * only if space is available else
      * waits for space to become available.
      */
     void put(E item)  throws InterruptedException ;


     /**
      * Retrieves and removes the head of this queue
      * only if elements are available else
      * waits for element to become available.
      */
     E take()  throws InterruptedException;
     
     /**
      * Returns size of queue.
      */
     int size();

}
