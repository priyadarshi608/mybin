import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sss.lock.lock.MyLock;
import sss.lock.lock.MyReentrantLock;

public class ThreadSafeArrayList {
	// We can check that why normal lock will not work in this case.
//	private MyLock lock = new MyLock();
	
	// This reentrant lock will work in this condition also.
	private MyReentrantLock lock = new MyReentrantLock();
	
	// This is also the reentrant lock provided by the java.util.concurrent.
//	private Lock lock = new ReentrantLock();
	
	private List<Integer> list = new ArrayList<Integer>();
	static int i = 0;
	
	public void set() {
		try {
			lock.lock();
			i++;
			checkReentrant();
			list.add(i);
			System.out.println(i + " added by " + Thread.currentThread().getName());
		} finally {
			lock.unlock();
		}
	}
	
	private void checkReentrant() {
		System.out.println("Entering checkReentrant..");
		lock.lock();
		System.out.println("In the locked area..");
		lock.unlock();
		System.out.println("Exiting checkReentrant..");
	}
}
