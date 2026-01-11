import sss.atomic.atomic.AtomicInteger;
import sss.atomic.atomic.AtomicLong;

class AtomicProcessingThread implements Runnable {
    private AtomicInteger count = new AtomicInteger();
    @Override
    public void run() {
        for (int i = 1; i < 5; i++) {
            processSomething(i);
            count.incrementAndGet();
        }
    }
    public int getCount() {
        return this.count.get();
    }
    private void processSomething(int i) {
        // processing some job
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
