package sss.atomic.atomic;

/**
 * @author Shubham Priyadarshi
 *
 * AtomicLongCustom provides you with long value that is updated atomically.
 */
public class AtomicLong{
	long currentValue;
	long previousValue;

	//AtomicLongCustom constructors >
	/**
	 * Creates a new AtomicLongCustom and is initialized to 0.
	 */
	public AtomicLong(){
		currentValue=0;
	}

	/**
	 * Creates a new AtomicLongCustom and is initialized to specified initialValue.
	 * @param initialValue
	 */
	public AtomicLong(long initialValue){
		currentValue=initialValue;
	}

	//AtomicLongCustom important Methods >
	/**
	 * method returns the current value
	 *
	 */
	public synchronized long get(){
		return currentValue;
	}

	/**
	 * Sets to newValue.
	 */
	public synchronized void set(long newValue){
		currentValue=newValue;
	}

	/**
	 * Sets to newValue and returns the old value.
	 */
	public synchronized long getAndSet(long newValue){
		previousValue=currentValue;
		currentValue=newValue;
		return previousValue;
	}

	/**
	 * Compare with expect, if equal, set to update and return true.
	 */
	public synchronized boolean compareAndSet(long expect, long update){
		if(currentValue == expect){
			currentValue=update;
			return true;
		}
		else
			return false;
	}

	//Addition methods >
	/**
	 * adds value to the current value. And return updated value.
	 */
	public synchronized long addAndGet(long value){
		return currentValue+=value;
	}

	/**
	 * increments current value by 1. And return updated value.
	 */
	public synchronized long incrementAndGet(){
		return ++currentValue;
	}

	/**
	 * Method return current value. And adds value to the current value.
	 */
	public synchronized long getAndAdd(long value){
		previousValue=currentValue;
		currentValue+=value;
		return previousValue;
	}

	/**
	 * Method return current value. And increments current value by 1.
	 *
	 */
	public synchronized long getAndIncrement(){
		return currentValue++;
	}

	//Subtraction methods >
	/**
	 * decrements current value by 1. And return updated value.
	 */
	public synchronized long decrementAndGet(){
		return --currentValue;
	}

	/**
	 * Method return current value. And decrements current value by 1.
	 */
	public synchronized long getAndDecrement(){
		return currentValue--;  
	}


	@Override
	public String toString() {
		return "AtomicLongCustom= " + currentValue ;
	}   
}
