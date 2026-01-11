package sss.threadpool.myblockingqueue;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementing custom LinkedBlockingQueue class.
 * This BlockingQueue implementation follows FIFO (first-in-first-out).
 * New elements are inserted at the tail of the queue,
 * and removal elements is done at the head of the queue.
 *
 * @author AnkitMittal
 * Copyright (c), AnkitMittal .
 * All Contents are copyrighted and must not be reproduced in any form.
 */
public class LinkedBlockingQueueCustom<E> implements BlockingQueueCustom<E>{

	private List<E> queue;
	private int  maxSize ; //maximum number of elements queue can hold at a time.

	public LinkedBlockingQueueCustom(int maxSize){
		this.maxSize = maxSize;
		queue = new LinkedList<E>();
	}


	/**
	 * Inserts the specified element into this queue
	 * only if space is available else
	 * waits for space to become available.
	 * After inserting element it notifies all waiting threads.
	 */
	public synchronized void put(E item)  throws InterruptedException  {

		//check space is available or not.
		if (queue.size() == maxSize) {
			this.wait();
		}

		//space is available, insert element and notify all waiting threads.
		queue.add(item);
		this.notifyAll();
	}


	/**
	 * Retrieves and removes the head of this queue
	 * only if elements are available else
	 * waits for element to become available.
	 * After removing element it notifies all waiting threads.
	 */
	public synchronized E take()  throws InterruptedException{

		//waits element is available or not.
		if (queue.size() == 0) {
			this.wait();
		}

		//element is available, remove element and notify all waiting threads.
		E removed = queue.remove(0);
		this.notifyAll();
		return removed;

	}

	/**
	 * Returns size of LinkedBlockingQueueCustom.
	 */
	public synchronized int size() {
		return queue.size();
	}

}
