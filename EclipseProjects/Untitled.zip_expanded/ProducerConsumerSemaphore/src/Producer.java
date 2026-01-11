class Producer implements Runnable {
	Q q;
	Producer(Q q, String threadName) {
		this.q = q;
		new Thread(this, threadName).start();
	}
	public void run() {
		int i = 0;
		while(true && i <= 9) {
			q.put(i++);
		}
	}
}
