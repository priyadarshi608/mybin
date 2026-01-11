package testing;
// This class is only for comparisons.
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Resource {
    public void doSomething(){
        //do some operation, DB read, write etc
    }
    public void doLogging(){
        //logging, no need for thread safety
    }
}
class SynchronizedLockExample implements Runnable{
	 
    private Resource resource;
     
    public SynchronizedLockExample(Resource r){
        this.resource = r;
    }
     
    @Override
    public void run() {
        synchronized (resource) {
            resource.doSomething();
        }
        resource.doLogging();
    }
}









class ConcurrencyLockExample implements Runnable{
 
    private Resource resource;
    private Lock lock;
     
    public ConcurrencyLockExample(Resource r){
        this.resource = r;
        this.lock = new ReentrantLock();
    }
     
    @Override
    public void run() {
        try {
            lock.tryLock(10, TimeUnit.SECONDS);
            resource.doSomething();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            //release lock
            lock.unlock();
        }
        resource.doLogging();
    }
}
