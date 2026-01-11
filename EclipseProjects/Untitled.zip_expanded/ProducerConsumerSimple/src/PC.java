class PC {
	public static void main(String args[]) {
		Q q = new Q();
		new Producer(q, "Producer-1");
		new Producer(q, "Producer-2");
		new Producer(q, "Producer-3");
		new Consumer(q, "Consumer-1");
		new Consumer(q, "Consumer-2");
		System.out.println("Press Control-C to stop.");
	}
}
