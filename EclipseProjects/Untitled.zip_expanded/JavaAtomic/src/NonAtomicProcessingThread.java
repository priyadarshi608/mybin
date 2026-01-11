
class NonAtomicProcessingThread implements Runnable {
	private int count;

    @Override
    public void run() {
        for (int i = 1; i <= 1000000; i++) {
        	count++;
        }
    }
    
    public int getCount() {
    	return count;
    }
}
