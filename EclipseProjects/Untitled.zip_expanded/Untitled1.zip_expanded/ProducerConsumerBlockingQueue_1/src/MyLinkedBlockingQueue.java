import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLinkedBlockingQueue<T> implements BlockingQueue<T>{

	private final int CAPACITY = 100000;
	private Object[] elems = null;
	private int current = 0;
	private int addIndex = 0;
	private int removeIndex = 0;
	
	private final Lock lock = new ReentrantLock();
	private final Condition isEmpty = lock.newCondition();
	private final Condition isFull = lock.newCondition();
	
	public MyLinkedBlockingQueue() {
		// ArrayList also does same thing. That also uses the array of Object 
		// because it is not possible to create the array of generic type.
		this.elems = new Object[CAPACITY];
	}
	
	@Override
	public void put(Object elem) throws InterruptedException {
		lock.lock();
		while(current >= elems.length)
			isFull.await();
	
		elems[addIndex] = elem;
		
		//We need the modulo, in order to avoid going out of bounds.
		addIndex = (addIndex + 1) % elems.length;
		
		++current;
		
		//Notify the consumer that there is data available.
		isEmpty.signal();
		
		lock.unlock();
	}

	@Override
	public T take() throws InterruptedException {
		Object elem = null;
		
		lock.lock();
		while(current <= 0)
			isEmpty.await();
	
		elem = elems[removeIndex];

		//We need the modulo, in order to avoid going out of bounds.
		removeIndex = (removeIndex + 1) % elems.length;
		
		--current;
		
		//Notify the producer that there is space available.
		isFull.signal();
		
		lock.unlock();
		
		return (T) elem;
	}

	@Override
	public int size() {
		return current;
	}

	@Override
	public	Iterator<T> iterator() {
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<T> {

		public MyIterator() {
		}

		public boolean hasNext(){
			return false;
		}

		// Gives the nodes in inorder
		public T next() {
			Object object = new Object();
			return (T) object;
		}
	}
	
	@Override
	public T remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(T e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(T e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(T e, long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int remainingCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		// TODO Auto-generated method stub
		return 0;
	}
}
