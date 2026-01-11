package sss.dpobjectpool.objectpool;

public interface ObjectPool<T> {
	
	/**
	 * Gets the next free object from the pool. If the pool doesn't contain any 
	 * objects, a new object will be created and given to the caller of this 
	 * method back.
	 * 
	 * @return borrowed object T
	 */
	public T borrowObject();
	
	/**
	 * Returns object back to the pool.
	 * 
	 * @param object: object to be returned
	 */
	public void returnObject(T obj);

	/**
	 * Shutdown the periodic checking of minimum and maximum number of objects.
	 */
	public void shutdown();
}
