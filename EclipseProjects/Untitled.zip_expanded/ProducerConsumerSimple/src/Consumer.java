class Consumer implements Runnable {
	Q q;
	Consumer(Q q, String threadName) {
		this.q = q;
		new Thread(this, threadName).start();
	}
	public void run() {
		while(true) {
			q.get();
		}
	}
}
