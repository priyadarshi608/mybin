// Java Program to illustrate the concept of interrupt() method while a thread does not stops working 
class MyThread extends Thread { 
	public void run() {
		try { 
			for (int i = 0; i < 5; i++) { 
				System.out.println("Child Thread executing");
				System.out.println("cccccccccccccccccccccc");
				synchronized (this) {
					wait();
				}
				System.out.println("cccccccccccccccccccccc");
				System.out.println("Child Thread awake from sleep");
			} 
		} 
		catch (InterruptedException e) { 
			System.out.println("InterruptedException occur"); 
		}
		System.out.println("But further running of thread is also possible.");
	}
} 
