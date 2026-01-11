package sss.atomic.atomic;

/**
 * @author Shubham Priyadarshi
 * AtomicInteger provides you with int value that is updated atomically.
 */
public class AtomicInteger{

	int currentValue;
	int previousValue;

	//AtomicintCustom constructors >
	/**
	 * Creates a new AtomicInteger and is initialized to 0.
	 */
	public AtomicInteger(){
		currentValue=0;
	}

	/**
	 * Creates a new AtomicInteger and is initialized to specified initialValue.
	 * @param initialValue
	 */
	public AtomicInteger(int initialValue){
		currentValue=initialValue;
	}

	//AtomicInteger  important Methods >
	/**
	 * method returns the current value
	 *
	 */
	public synchronized int get(){
		return currentValue;
	}

	/**
	 * Sets to newValue.
	 */
	public synchronized void set(int newValue){
		currentValue=newValue;
	}

	/**
	 * Sets to newValue and returns the old value.
	 */
	public synchronized int getAndSet(int newValue){
		previousValue=currentValue;
		currentValue=newValue;
		return previousValue;
	}

	/**
	 * Compare with expect, if equal, set to update and return true.
	 */
	public synchronized boolean compareAndSet(int expect, int update){
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
	public synchronized int addAndGet(int value){
		return currentValue+=value;
	}

	/**
	 * increments current value by 1. And return updated value.
	 */
	public synchronized int incrementAndGet(){
		return ++currentValue;
	}

	/**
	 * Method return current value. And adds value to the current value.
	 */
	public synchronized int getAndAdd(int value){
		previousValue=currentValue;
		currentValue+=value;
		return previousValue;
	}

	/**
	 * Method return current value. And increments current value by 1.
	 *
	 */
	public synchronized int getAndIncrement(){
		return currentValue++;
	}

	//Subtraction methods >
	/**
	 * decrements current value by 1. And return updated value.
	 */
	public synchronized int decrementAndGet(){
		return --currentValue;
	}

	/**
	 * Method return current value. And decrements current value by 1.
	 */
	public synchronized int getAndDecrement(){
		return currentValue--;  
	}


	@Override
	public String toString() {
		return "AtomicInteger = " + currentValue ;
	}   

}
