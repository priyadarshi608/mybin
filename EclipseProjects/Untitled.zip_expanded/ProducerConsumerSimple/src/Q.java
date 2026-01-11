// A correct implementation of a producer and consumer.
class Q {
	int n;
	boolean isValueSet = false;
	synchronized int get() {
		while(!isValueSet) {
			try {
				wait();
			} catch(InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		System.out.println(Thread.currentThread().getName() + " Got: " + n);
		isValueSet = false;
		notifyAll();
		return n;
	}
	synchronized void put(int n) {
		while(isValueSet) {
			try {
				wait();
			} catch(InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		this.n = n;
		isValueSet = true;
		System.out.println(Thread.currentThread().getName() + " Put: " + n);
		notifyAll();
	}
}
