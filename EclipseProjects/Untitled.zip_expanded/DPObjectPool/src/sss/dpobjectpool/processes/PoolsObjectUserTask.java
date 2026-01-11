package sss.dpobjectpool.processes;

import sss.dpobjectpool.objectpool.ObjectPool;

public class PoolsObjectUserTask implements Runnable {
	private ObjectPool<MyObject> pool;  
	private int threadNo;  
	public PoolsObjectUserTask(ObjectPool<MyObject> pool, int threadNo){  
		this.pool = pool;  
		this.threadNo = threadNo;
	}  

	public void run() {  
		// get an object from the pool  
		MyObject exportingProcess = pool.borrowObject();  
		System.out.println("Thread " + threadNo + ": Object with process no. "  
				+ exportingProcess.getProcessNo() + " was borrowed");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// .........  
		// return ExportingProcess instance back to the pool  
		pool.returnObject(exportingProcess);

		System.out.println("Thread " + threadNo +": Object with process no. "  
				+ exportingProcess.getProcessNo() + " was returned");  
	}  
}
