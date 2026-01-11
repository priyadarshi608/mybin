import java.util.Iterator;

public class CircularArray<T> implements Iterable<T> {
	
	private T[] items;
	private int head = 0;
	
	@SuppressWarnings("unchecked")
	public CircularArray(int size) {
		items = (T[]) new Object[size];
	}
	
	public void rotateLeft(int shift) {
		head = convert(shift);
	}
	
	public void rotateRight(int shift) {
		head = convert(-1 * shift);
	}
	
	public T get(int i) {
		if (i < 0 || i >= items.length) {
			throw new java.lang.IndexOutOfBoundsException("Index " + i + " is out of bounds");
		}
		return items[convert(i)];
	}
	
	public void set(int i, T item) {
		items[convert(i)] = item;
	}
	
	public Iterator<T> iterator() {
		return new CircularArrayIterator();
	}
	
	private class CircularArrayIterator implements Iterator<T> {
		private int _current = -1;
		
		public CircularArrayIterator() { }
		
		@Override
		public boolean hasNext() {
			return _current < items.length - 1;
		}
		
		@Override
		public T next() {
			_current++;
			return (T) items[convert(_current)];
		}
		
	}
	
	private int convert(int index) {
		index = index % items.length;
		if (index < 0) {
			index += items.length;
		}
		return (head + index) % items.length;
	}
	
}
