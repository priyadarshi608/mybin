package sss.deadlock_3.resource;

public class Resource2 {
	boolean isLocked = false;
	
	synchronized public void first(Resource1 a) {
		isLocked = true;
		String name = Thread.currentThread().getName();
		System.out.println(name + " entered Resource2.bar");
		while (!a.isLocked) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				System.out.println("Resource2 Interrupted");
			}
		}
		System.out.println(name + " trying to call Resource1.last()");
		a.last();
	}
	synchronized public void last() {
		System.out.println("Inside Resource1.last");
	}
}
