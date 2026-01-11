package sss.dll.list;

/**
 * This interface for this Doubly-Linked List. The index of the Doubly-Linked 
 * List will be 0 to (size - 1).
 * @author shubham
 */
public interface List<E> {
	/**
	 * Returns the size of the DLL.
	 * @return size the size of the DLL.
	 */
	int size();
	
	/**
	 * Adds the element e at the end of the DLL.
	 * @param e the element to be added.
	 */
	void add(E e);
	
	/**
	 * Adds the element e at the given index.
	 * @param index the position at which the element needs to be added.
	 * @param e the element to be added
	 */
	void add(int index, E e);
	
	/**
	 * Adds the element e at the end of the DLL.
	 * @param e the element to be added.
	 */
	void addLast(E e);
	
	/**
	 * Adds the element e at the start of the DLL.
	 * @param e the element to be added.
	 */
	void addFirst(E e);
	
	/**
	 * Removes the element from the given position.
	 * @param index the index to delete the node.
	 * @return the removed element.
	 */
	E remove(int index);
	
	/**
	 * Removes the element from the first position. 
	 * @return the first element.
	 */
	E removeFirst();
	
	/**
	 * Removes the element from the last position.
	 * @return the last element.
	 */
	E removeLast();
	
	/**
	 * Sets the element at the given index.
	 * @param index the index at which we have to set the element.
	 * @param e the element to be set.
	 * @return the previous element present at the given index.
	 */
	E set(int index, E e);
	
	/**
	 * Returns the element from the given index.
	 * @param index the index whose element is required.
	 * @return the element at the given index.
	 */
	E index(int index);

	/**
	 * @return the iterator for the DLL.
	 */
	Iterator<E> getDLLIterator();
	
	/**
	 * @return the reverse iterator for the DLLL.
	 */
	Iterator<E> getDLLReverseIterator();
}
