package sss.lock.lock;

public class MyLock {
	private boolean isLocked = false;
	
	synchronized public void lock() {
		while (isLocked) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isLocked = true;
	}
	
	synchronized public void unlock() {
		isLocked = false;
		notify();
	}
}
