package synchronizedBlock_1;

class Caller implements Runnable {
    String msg;
    Callme target;
    Thread t;
    public Caller(Callme targ, String s) {
    	target = targ;
    	msg = s;
    	t = new Thread(this);
    	t.start();
    }
    public void run() {
    	synchronized(target) { // synchronized block
    		target.call(msg);
    	}
    }
}
