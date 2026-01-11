
public class JavaAtomicDemo {
	
    public static void main(String[] args) throws InterruptedException {
//    	NonAtomicProcessingThread pt = new NonAtomicProcessingThread();
//        AtomicProcessingThread pt = new AtomicProcessingThread();
        NonBlockingWorkingAtomicProcessingThread pt = new NonBlockingWorkingAtomicProcessingThread();
//        NonBlockingNonWorkingAtomicProcessingThread pt = new NonBlockingNonWorkingAtomicProcessingThread();
        Thread t1 = new Thread(pt, "t1");
        Thread t2 = new Thread(pt, "t2");
        Thread t3 = new Thread(pt, "t3");
        Thread t4 = new Thread(pt, "t4");
        Thread t5 = new Thread(pt, "t5");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        System.out.println("Processing count=" + pt.getCount());
    }
}
