import java.util.concurrent.Phaser;

/** Copyright (c), AnkitMittal JavaMadeSoEasy.com */
public class PhaserDemo {
	public static void main(String[] args) {

		/*Creates a new phaser with 1 registered unArrived parties 
		 * and initial phase number is 0
		 */
		Phaser phaser=new Phaser(1);
		System.out.println("new phaser with 1 registered unArrived parties"
				+ " created and initial phase  number is 0 ");

		//Create 3 threads
		Thread thread1=new Thread(new MyRunnable(phaser,"first"),"Thread-1");
		Thread thread2=new Thread(new MyRunnable(phaser,"second"),"Thread-2");
		Thread thread3=new Thread(new MyRunnable(phaser,"third"),"Thread-3");

		System.out.println("\n--------Phaser has started---------------");
		//Start 3 threads
		thread1.start();
		thread2.start();
		thread3.start();

		//get current phase
		int currentPhase=phaser.getPhase();
		/*arriveAndAwaitAdvance() will cause thread to wait until current phase
		 * has been completed i.e. until all registered threads
		 * call arriveAndAwaitAdvance()
		 */
		phaser.arriveAndAwaitAdvance();
		System.out.println("------Phase-"+currentPhase+" has been COMPLETED----------");

		//------NEXT PHASE BEGINS------

		currentPhase=phaser.getPhase();
		phaser.arriveAndAwaitAdvance();
		System.out.println("------Phase-"+currentPhase+" has been COMPLETED----------");

		/* current thread Arrives and deRegisters from phaser.
		 * DeRegistration reduces the number of parties that may
		 * be required to advance in future phases.
		 */
		phaser.arriveAndDeregister();

		//check whether phaser has been terminated or not.
		if(phaser.isTerminated())
			System.out.println("\nPhaser has been terminated");

	} 
}
