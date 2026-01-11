package sss.deadlock_3.resource;

//An example of deadlock.
public class Resource1 {
	boolean isLocked = false;
	
	synchronized public void first(Resource2 b) {
		isLocked = true;
		String name = Thread.currentThread().getName();
		System.out.println(name + " entered Resource1.foo");
		while (!b.isLocked) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				System.out.println("Resource1 Interrupted");
			}
		}
		System.out.println(name + " trying to call Resource2.last()");
		b.last();
	}
	synchronized public void last() {
		System.out.println("Inside Resource2.last");
	}
}
