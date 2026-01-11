// Java Program to illustrate the concept of interrupt() method while a thread does not stops working 
class MyThread extends Thread { 
	public void run() {
		try { 
			for (int i = 0; i < 5; i++) { 
				System.out.println("Child Thread executing");
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");

				// Here current threads goes to sleeping state 
				// Another thread gets the chance to execute 
				Thread.sleep(20000);
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				System.out.println("Child Thread awake from sleep");
			} 
		} 
		catch (InterruptedException e) { 
			System.out.println("InterruptedException occur"); 
		}
		System.out.println("But further running of thread is also possible.");
	}
} 
