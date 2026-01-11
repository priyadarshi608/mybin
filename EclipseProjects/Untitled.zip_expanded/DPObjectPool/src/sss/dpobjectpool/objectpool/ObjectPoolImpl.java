package sss.dpobjectpool.objectpool;

import java.util.concurrent.ConcurrentLinkedQueue;  
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  

public abstract class ObjectPoolImpl<T> implements ObjectPool<T> {

	/**
	 * Pool implementation is based on ConcurrentLinkedQueue. It is a
	 * thread-safe queue based on linked nodes.
	 */
	private ConcurrentLinkedQueue<T> pool;  

	/**
	 * ScheduledExecutorService starts a special task in a separate thread and 
	 * observes the minimum and maximum number of objects in the pool 
	 * periodically in a specified time (with parameter validationInterval).
	 * When the number of objects is less than the minimum, missing instances 
	 * will be created. When the number of objects is greater than the maximum, 
	 * too many instances will be removed.
	 */
	private ScheduledExecutorService scheduledExecutorService;  

	/**
	 * Creates the pool.
	 * 
	 * @param minObjects: the minimum number of objects residing in the pool
	 */
	public ObjectPoolImpl(final int minObjects)   
	{  
		initialize(minObjects);  
	}  

	/**
	 * Creates the pool.
	 * 
	 * @param minObjects: minimum number of objects residing in the pool.
	 * @param maxObjects: maximum number of objects residing in the pool.
	 * @param validationInterval: time in seconds for periodical checking of 
	 * minObjects / maxObjects conditions in a separate thread. When the number 
	 * of objects is less than minObjects, missing instances will be created. 
	 * When the number of objects is greater than maxObjects, too many instances 
	 * will be removed.
	 */
	public ObjectPoolImpl(final int minObjects,
			final int maxObjects, 
			final long validationInterval) {  
		// initialize pool  
		initialize(minObjects);  
		// check pool conditions in a separate thread  
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override  
			public void run() {  
				int size = pool.size();  

				if (size < minObjects) {
					int sizeToBeAdded = minObjects - size;  
					for (int i = 0; i < sizeToBeAdded; i++) {  
						pool.add(createObject());  
					}  
				} else if (size > maxObjects) {  
					int sizeToBeRemoved = size - maxObjects;  
					for (int i = 0; i < sizeToBeRemoved; i++) {  
						pool.poll();  
					}  
				}  
			}  
		}, validationInterval, validationInterval, TimeUnit.SECONDS);  
	}

	/**
	 * {@inheritDoc}
	 */
	public T borrowObject() {  
		T object;
		if ((object = pool.poll()) == null) {
			object = createObject();  
		}  
		return object;  
	}

	/**
	 * {@inheritDoc}
	 */
	public void returnObject(T object) {  
		if (object == null) {  
			return;  
		}  
		this.pool.offer(object);  
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown(){  
		if (scheduledExecutorService != null){  
			scheduledExecutorService.shutdown();  
		}  
	}

	/**
	 * Creates a new object.
	 * @return new object T.
	 */
	protected abstract T createObject();  

	/**
	 * This method initializes the ConcurrentLinkedQueue with minimum number of
	 * objects required.
	 */
	private void initialize(final int minObjects)  {  
		pool = new ConcurrentLinkedQueue<T>();  
		for (int i = 0; i < minObjects; i++) {  
			pool.add(createObject());  
		}  
	}  
}
