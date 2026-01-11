package sss.dll.list;

import java.util.NoSuchElementException;

import sss.dll.myexceptions.InvalidElementException;
import sss.dll.myexceptions.InvalidIndexException;

/**
 * Concrete implementation class of the DoublyLinkedList interface. This class
 * implementation provides the DoublyLinkedList. The index of the elements of
 * the List is from 0 to (size - 1). This DLL implementation is not thread-safe.
 * Its iterators are not fail-fast.
 * 
 * @author shubham
 */
public class DoublyLinkedList<E> implements List<E> {
	private int size = 0; // The size of the DLL
	
	// First and last references of the DLL
	private Node<E> first;
	private Node<E> last;

	/**
	 * Constructs the Doubly Linked List object.
	 */
	public DoublyLinkedList() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void add(E e) {
		addLast(e);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void add(int index, E e) {
		if (index == 0)
			addFirst(e);
		else if (index == size)
			addLast(e);
		else {
			checkIndexValidity(index);
			Node<E> newNode = createNode(e);
			Node<E> node = node(index);
			newNode.next = node;
			newNode.prev = node.prev;
			node.prev.next = newNode;
			node.prev = newNode;
			size++;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addLast(E e) {
		if (size == 0)
			addFirstNode(e);
		else {
			Node<E> newNode = createNode(e);
			last.next = newNode;
			newNode.prev = last;
			last = newNode;
			size++;
		}
	}
	
	// Adds the first element in the  empty DLL.
	private void addFirstNode(E e) {
		Node<E> newNode = createNode(e);
		first = newNode;
		last = newNode;
		size++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addFirst(E e) {
		if (size == 0)
			addFirstNode(e);
		else {
			Node<E> newNode = createNode(e);
			first.prev = newNode;
			newNode.next = first;
			first = newNode;
			size++;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public E remove(int index) {
		checkIndexValidity(index);
		if (size == 1)
			return removeSizeOne();
		else {
			if (index == 0)
				return removeFirst();
			else if (index == size - 1)
				return removeLast();
			else {
				Node<E> node = node(index);
				node.prev.next = node.next;
				node.next.prev = node.prev;
				size--;
				return node.item;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public E removeFirst() {
		if (size == 0)
			throw new NoSuchElementException();
		else {
			Node<E> temp = first;
			first.next.prev = null;
			first = first.next;
			size--;
			return temp.item;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public E removeLast() {
		if (size == 0)
			throw new NoSuchElementException();
		else {
			Node<E> temp = last;
			last.prev.next = null;
			last = last.prev;
			size--;
			return temp.item;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public E set(int index, E e) {
		checkIndexValidity(index);
		Node<E> node = node(index);
		E eFormer = node.item;
		node.item = e;
		return eFormer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public E index(int index) {
		checkIndexValidity(index);
		Node<E> node = node(index);
		return node.item;
	}
	
	private E removeSizeOne() {
		Node<E> temp = first;
		first = null;
		last = null;
		size--;
		return temp.item;
	}

	
	
	/**
	 * Wrapper node class containing elements
	 * @author shubham
	 * @param <E>
	 */
	private static class Node<E> {
		E item;
		Node<E> prev;
		Node<E> next;
		
		Node(E item, Node<E> prev, Node<E> next) {
			this.item = item;
			this.prev = prev;
			this.next = next;
		}
	}

    private Node<E> createNode(E e) {
    	if (e == null)
    		throw new InvalidElementException();
    	return new Node<E>(e, null, null);
    }
    
	private Node<E> node(int index) {
		checkIndexValidity(index);
		Node<E> temp;
		if (index < (size >> 1)) {
			temp = first;
			for (int i = 0; i < index; i++) {
				temp = temp.next;
			}
		} else {
			temp = last;
			for (int i = size - 1; i > index; i--) {
				temp = temp.prev;
			}
		}
		return temp;
	}
	
	private void checkIndexValidity(int index) {
		if (!isValidIndex(index))
			throw new InvalidIndexException(outOfBoundMessage(index));
	}
	private String outOfBoundMessage(int index) {
		return ("size: " + size + ", index: " + index);
	}
	
	private boolean isValidIndex(int index) {
		return (index >= 0 && index < size);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> getDLLIterator() {
		return new DLLIteratorImpl(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> getDLLReverseIterator() {
		return new DLLReverseIteratorImpl();
	}
	
	/**
	 * Concrete implementation class of the DLL iterator
	 * @author shubham
	 */
	private class DLLIteratorImpl implements Iterator<E> {
		private Node<E> next;
		private int nextIndex = 0;

		/**
		 * Constructs the DLL iterator.
		 * @param index initial position.
		 */
		DLLIteratorImpl(int index) {
			nextIndex = index;
			next = (index == size) ? null : node(index);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean hasNext() {
			return nextIndex < size;
		}

		/**
		 * {@inheritDoc}
		 */
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Node<E> temp = next;
			next = next.next;
			nextIndex++;
			return temp.item;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasPrev() {
			return nextIndex > 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public E prev() {
			if (!hasPrev())
				throw new NoSuchElementException();
			next = (next == null) ? last : next.prev;
			nextIndex--;
			return next.item;
		}
	}
	
	/**
	 * Adaptor class of DLLIteratorImpl to iterate the list backward
	 * @author shubham
	 */
	private class DLLReverseIteratorImpl implements Iterator<E> {
		private final Iterator<E> dllIterator = new DLLIteratorImpl(size);
		
		/**
		 * {@inheritDoc}
		 */
		public boolean hasNext() {
			return dllIterator.hasPrev();
		}

		/**
		 * {@inheritDoc}
		 */
		public E next() {
			return dllIterator.prev();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasPrev() {
			return dllIterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		public E prev() {
			return dllIterator.next();
		}
	}
}
