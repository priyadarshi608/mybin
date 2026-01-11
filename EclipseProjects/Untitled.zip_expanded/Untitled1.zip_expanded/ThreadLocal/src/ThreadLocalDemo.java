import java.util.Random;

public class ThreadLocalDemo {
	public static void main(String[] args) throws InterruptedException {
		MyThreadLocalTask obj = new MyThreadLocalTask();
		for(int i=0 ; i<10; i++){
			Thread t = new Thread(obj, ""+i);
			Thread.sleep(new Random().nextInt(1000));
			t.start();
		}
	}
}
