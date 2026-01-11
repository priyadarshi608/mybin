package sss.dll.list;

/**
 * An iterator over Linked-List.
 * @author shubham
 */
public interface Iterator<E> {
	/**
	 * @return true if more elements present in forward direction in the list.
	 */
	boolean hasNext();
	
	/**
	 * Gives the reference of the next element.
	 * @return the reference of the next element.
	 */
	E next();
	
	/**
	 * @return true if more elements present in reverse direction in the list.
	 */
	boolean hasPrev();
	
	/**
	 * Gives the reference of the previous element.
	 * @return the reference of the previous element.
	 */
	E prev();
}
