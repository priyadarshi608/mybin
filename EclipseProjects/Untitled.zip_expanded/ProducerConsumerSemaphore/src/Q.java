import java.util.concurrent.Semaphore;

class Q {
	// an item 
	int n;
	
	// semCon initialized with 0 permits 
	// to ensure put() executes first 
	static Semaphore semProd = new Semaphore(1);
	static Semaphore semCon = new Semaphore(0);
	
	// to get an item from buffer 
	int get() {
		try { 
			// Before consumer can consume an item, 
			// it must acquire a permit from semCon 
			semCon.acquire(); 
		} catch(InterruptedException e) {
			System.out.println("InterruptedException caught"); 
		} 
		
		int temp = n;
		// consumer consuming an item 
		System.out.println(Thread.currentThread().getName() + " Got: " + temp);
		
		// After consumer consumes the item, 
		// it releases semProd to notify producer 
		semProd.release(); 
		return temp;
	} 
	
	// to put an item in buffer 
	void put(int n) {
		try { 
			// Before producer can produce an item, 
			// it must acquire a permit from semProd 
			semProd.acquire(); 
		} catch(InterruptedException e) { 
			System.out.println("InterruptedException caught"); 
		} 
		
		// producer producing an item 
		this.n = n; 
		
		System.out.println(Thread.currentThread().getName() + " Put: " + n);
		
		// After producer produces the item, 
		// it releases semCon to notify consumer 
		semCon.release(); 
	} 
} 
