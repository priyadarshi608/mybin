import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.atomic.AtomicLong;

import sss.dpobjectpool.objectpool.ObjectPool;
import sss.dpobjectpool.objectpool.ObjectPoolImpl;
import sss.dpobjectpool.processes.MyObject;
import sss.dpobjectpool.processes.PoolsObjectUserTask;  

public class ObjectPoolDemo{  
	private ObjectPool<MyObject> pool;
	private AtomicLong objectNo=new AtomicLong(0);
	public void setUp() {
		// Create a pool of objects of type Process.  
		/*Parameters: 
             1) Minimum number of special Process instances residing in the pool = 4 
             2) Maximum number of special Process instances residing in the pool = 10 
             3) Time in seconds for periodical checking of minObjects / maxObjects conditions 
                in a separate thread = 5. 
             -->When the number of Process instances is less than minObjects,  
                 missing instances will be created. 
             -->When the number of Process instances is greater than maxObjects, 
                  too many instances will be removed. 
             -->If the validation interval is negative, no periodical checking
                  of minObjects / maxObjects conditions in a separate thread 
                  take place.
              These boundaries are ignored then. 
		*/
		
		pool = new ObjectPoolImpl<MyObject>(4, 4, 1) {
		// pool = new ObjectPoolImpl<Process>(4) {
			protected MyObject createObject()  
			{  
				// create a test object which takes some time for creation  
				return new MyObject(objectNo.incrementAndGet());  
			}
		};  
	}
	public void tearDown() {
		pool.shutdown();  
	}
	public void testObjectPool() {
//		for (int i = 0; i < 40; i++) {
//			new Thread(new PoolsObjectUserTask(pool, i)).start();
//		}
		// You can change the parameter of Executors.newFixedThreadPool() method
		// to see the different effect. From this place we can control the
		// number of threads created at a time.
		ExecutorService executor = Executors.newFixedThreadPool(8);

		// execute 8 tasks in separate threads  

		executor.execute(new PoolsObjectUserTask(pool, 1));
		executor.execute(new PoolsObjectUserTask(pool, 2));  
		executor.execute(new PoolsObjectUserTask(pool, 3));  
		executor.execute(new PoolsObjectUserTask(pool, 4));
		executor.execute(new PoolsObjectUserTask(pool, 5));  
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		executor.execute(new PoolsObjectUserTask(pool, 6));  
		executor.execute(new PoolsObjectUserTask(pool, 7));  
		executor.execute(new PoolsObjectUserTask(pool, 8));  
		executor.execute(new PoolsObjectUserTask(pool, 9));  
		executor.execute(new PoolsObjectUserTask(pool, 10));  
		executor.execute(new PoolsObjectUserTask(pool, 11));  

		executor.shutdown();
		try {  
			executor.awaitTermination(30, TimeUnit.SECONDS);  
		} catch (InterruptedException e) {
			e.printStackTrace();  
		}  
	}  
	public static void main(String args[])  {   
		ObjectPoolDemo op=new ObjectPoolDemo();  
		op.setUp();
		op.testObjectPool();  
		op.tearDown();  
	}   
}
