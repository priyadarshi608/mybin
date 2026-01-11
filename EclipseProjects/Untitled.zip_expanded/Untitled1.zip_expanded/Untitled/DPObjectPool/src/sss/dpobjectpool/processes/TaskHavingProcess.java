package sss.dpobjectpool.processes;

import sss.dpobjectpool.objectpool.ObjectPool;

public class TaskHavingProcess implements Runnable {  
	private ObjectPool<Process> pool;  
	private int threadNo;  
	public TaskHavingProcess(ObjectPool<Process> pool, int threadNo){  
		this.pool = pool;  
		this.threadNo = threadNo;
	}  

	public void run() {  
		// get an object from the pool  
		Process exportingProcess = pool.borrowObject();  
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
