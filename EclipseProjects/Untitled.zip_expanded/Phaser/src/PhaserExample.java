import java.util.concurrent.Phaser;

public class PhaserExample {
	public static void main(String[] args) throws InterruptedException {
		Phaser phaser = new Phaser();
		phaser.register();//register self... phaser waiting for 1 party (thread)
		int phasecount = phaser.getPhase();
		System.out.println("Phasecount is "+phasecount);
		new PhaserExample().testPhaser(phaser,200);//phaser waiting for 2 parties
		new PhaserExample().testPhaser(phaser,400);//phaser waiting for 3 parties
		new PhaserExample().testPhaser(phaser,600);//phaser waiting for 4 parties
		//now that all threads are initiated, we will de-register main thread 
		//so that the barrier condition of 3 thread arrival is meet.
		phaser.arriveAndDeregister();
		Thread.sleep(8000);
		phasecount = phaser.getPhase();
		System.out.println("Phasecount is "+phasecount);

	}

	private void testPhaser(final Phaser phaser,final int sleepTime) {
		phaser.register();
		new Thread(){
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					try	{
						Thread.sleep(sleepTime);
						System.out.println(Thread.currentThread().getName()+" arrived, i = " + i);
						phaser.arriveAndAwaitAdvance();//threads register arrival to the phaser.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName()+" after passing barrier, i = " + i);
				}
			}
		}.start();
	}
}