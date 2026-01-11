package sss.atomic.atomic;

/**
 * @author Shubham Priyadarshi
 * AtomicInteger provides you with int value that is updated atomically.
 */
public class NonBlockingNonWorkingAtomicInteger{

	int currentValue;
	int previousValue;

	//AtomicintCustom constructors >
	/**
	 * Creates a new AtomicInteger and is initialized to 0.
	 */
	public NonBlockingNonWorkingAtomicInteger(){
		currentValue=0;
	}

	/**
	 * Creates a new AtomicInteger and is initialized to specified initialValue.
	 * @param initialValue
	 */
	public NonBlockingNonWorkingAtomicInteger(int initialValue){
		currentValue=initialValue;
	}

	//AtomicInteger  important Methods >
	/**
	 * method returns the current value
	 *
	 */
	public int get(){
		return currentValue;
	}

	/**
	 * Sets to newValue.
	 */
	public void set(int newValue){
		currentValue=newValue;
	}

	/**
	 * Sets to newValue and returns the old value.
	 */
	public int getAndSet(int newValue){
		previousValue=currentValue;
		currentValue=newValue;
		return previousValue;
	}

	/**
	 * Compare with expect, if equal, set to update and return true.
	 */
	public boolean compareAndSet(int expect, int update){
		if(currentValue == expect){
			currentValue=update;
			return true;
		}
		else
			return false;
	}

	/**
	 * increments current value by 1. And return updated value.
	 */
	public int incrementAndGet(){
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next))
                return next;
        }
	}

}
