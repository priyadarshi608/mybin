// Java Program to illustrate the concept of interrupt() method while a thread does not stops working 
class MyThread extends Thread { 
	public void run() {
		System.out.println("going to do task..");
		int k = 0;
		for (int i = 0; i < 100000; i++) {
			k++;
			for (int j = 0; j < k; j++) {
				j++;
			}
		}
		System.out.println("But further running of thread is also possible.");
	}
} 
