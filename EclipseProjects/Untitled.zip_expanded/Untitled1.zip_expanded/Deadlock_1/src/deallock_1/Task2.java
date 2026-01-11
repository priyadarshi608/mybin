package deallock_1;

public class Task2 implements Runnable {
	Resource1 a;
	Resource2 b;
	
	public Task2(Resource1 a, Resource2 b) {
		this.a = a;
		this.b = b;
	}
	
	public void run() {
		b.first(a);
	}
}
