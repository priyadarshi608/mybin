import sss.atomic.atomic.NonBlockingNonWorkingAtomicInteger;

public class NonBlockingNonWorkingAtomicProcessingThread implements Runnable {
	
    private NonBlockingNonWorkingAtomicInteger count = new NonBlockingNonWorkingAtomicInteger();
    
    @Override
    public void run() {
        for (int i = 1; i <= 1000000; i++) {
            count.incrementAndGet();
        }
    }
    
    public int getCount() {
        return this.count.get();
    }
}
