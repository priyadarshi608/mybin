package sss.lock.lock;

public class MyReentrantLock {
	private boolean isLocked = false;
	private Thread lockingThread = null;
	private int numLocks = 0;
	
	synchronized public void lock() {
		Thread currentThread = Thread.currentThread();
		while (isLocked && lockingThread != currentThread) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isLocked = true;
		lockingThread = currentThread;
		numLocks++;
	}
	
	synchronized public void unlock() {
		if (lockingThread == Thread.currentThread()) {
			numLocks--;
			if (numLocks == 0) {
				isLocked =  false;
				notify();
			}
		}
	}
}
