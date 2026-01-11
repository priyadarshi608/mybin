package testing;

public class InterruptThread {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new WaitRunnable());
        thread1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        thread1.interrupt();
	    }
	    private static class WaitRunnable implements Runnable {
	        @Override
	        public void run() {
	            System.out.println("Current time millis : " + System.currentTimeMillis());
	            try {
	                Thread.sleep(50000);
	            } catch (InterruptedException e) {
                System.out.println("The thread has been interrupted");
                System.out.println("The thread is interrupted : " + Thread.currentThread().isInterrupted());
            }
            System.out.println("Current time millis : " + System.currentTimeMillis());
        }
    }
}

//class UglyRunnable implements Runnable {
//    @Override
//    public void run() {
//        while(!Thread.currentThread().isInterrupted()){
//            //Heavy operation
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //Other operation
//        }
//    }
//}

//class BetterRunnable implements Runnable {
//    @Override
//    public void run() {
//        while(!Thread.currentThread().isInterrupted()){
//            //Heavy operation
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//            //Other operation
//        }
//    }
//}