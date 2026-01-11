package sss.bst.binary_tree;

import java.util.Iterator;

/**
 * A generic binary tree interface which provides all the important binary tree 
 * operations.
 * @author shubham
 * @param <E> the elements going to be stored in the nodes of the tree.
 */
public interface BinaryTree<E> {
	/**
	 * Inserts the data in the tree.
	 * @param data
	 */
	void insert(E data);
	
	/**
	 * Deletes the data in the tree.
	 * @param toDelete
	 */
	void delete(E toDelete);
	
	/**
	 * Searches the element in the binary tree
	 * @param toSearch the element to search
	 * @return returns true if the element is found
	 */
	boolean search(E toSearch);
	
	void preOrderTraversal();
	void inOrderTraversal();
	void postOrderTraversal();
	
	/**
	 * Uses the iterator implemented in the binary tree and prints the Binary Tree
	 * in the sequence given by the iterator.
	 * @return returns the string.
	 */
	String toString();
	
	/**
	 * @return returns the height of the Binary Tree
	 */
	int height();
	
	/**
	 * @return the total number of leaves in the Binary Tree
	 */
	int countLeaves();
	
	/**
	 * @return the width of the Binary Tree
	 */
	int width();
	
	/**
	 * @return the diameter of the Binary Tree
	 */
	int diameter();
	
	/**
	 * @return the clone of the Binary Tree invoking this method
	 */
	BinaryTree<E> clone();
	
	/**
	 * It replaces the tree nodes with the new tree nodes whose pre-order
	 * traversal and in-order traversal array will be given.
	 * @param pre pre-order-traversal array
	 * @param in in-order-traversal array
	 */
	void restore(E[] pre, E[] in);
	
	/**
	 * @return the iterator for the Binary tree.
	 */
	Iterator<E> iterator();
}
