package deallock_1;

public class Task1 implements Runnable {
	Resource1 a;
	Resource2 b;
	
	public Task1(Resource1 a, Resource2 b) {
		this.a = a;
		this.b = b;
	}
	
	public void run() {
		a.first(b);
	}
}
