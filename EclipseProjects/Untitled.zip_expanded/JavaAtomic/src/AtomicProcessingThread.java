import sss.atomic.atomic.AtomicInteger;

class AtomicProcessingThread implements Runnable {
    private AtomicInteger count = new AtomicInteger();
    
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
