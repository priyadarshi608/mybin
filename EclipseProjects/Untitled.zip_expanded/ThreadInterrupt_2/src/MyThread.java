// Java Program to illustrate the concept of interrupt() method while a thread does not stops working 
class MyThread extends Thread { 
	public void run() {
		int k = 0;
		for (int i = 0; i < 100000; i++) {
			k++;
			for (int j = 0; j < k; j++) {
				j++;
			}
		}
		
		try {
			System.out.println("Child Thread executing");
			System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbb");
			Thread.sleep(20000);
			System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbb");
			System.out.println("Child Thread awake from sleep");
		} 
		catch (InterruptedException e) { 
			System.out.println("InterruptedException occur"); 
		}
		System.out.println("But further running of thread is also possible.");
	}
} 
