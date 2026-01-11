package sss.binarytree.avl;

import java.util.Iterator;

/**
 * A generic binary tree interface which provides all the important binary tree 
 * operations.
 * @author shubham
 * @param <E> the elements going to be stored in the nodes of the tree.
 */
public interface BinaryTree<T> {
	
	/**
	 * Inserts the data in the tree.
	 * @param data
	 */
	void insert(T data);
	
	/**
	 * Deletes the data in the tree.
	 * @param toDelete
	 */
	void delete(T toDelete);
	
	/**
	 * Searches the element in the binary tree
	 * @param toSearch the element to search
	 * @return returns true if the element is found
	 */
	boolean search(T toSearch);
	
	/**
	 * Uses the iterator implemented in the binary tree and prints the Binary Tree
	 * in the sequence given by the iterator.
	 * @return returns the string.
	 */
	String toString();

	/**
	 * This method will print the tree in tree like format.
	 */
	void printInTreeFormat();
	
	/**
	 * @return returns the height of the Binary Tree
	 */
	int height1();
	
	/**
	 * @return the width of the Binary Tree
	 */
	int width();
	
	/**
	 * @return the diameter of the Binary Tree
	 */
	int diameter();
	
	/**
	 * @return the total number of leaves in the Binary Tree
	 */
	int countLeaves();
	
	/**
	 * prints preOrderTraversal of the binary tree.
	 */
	void preOrderTraversal();
	
	/**
	 * prints inOrderTraversal of the binary tree.
	 */
	void inOrderTraversal();
	
	/**
	 * prints postOrderTraversal of the binary tree.
	 */
	void postOrderTraversal();
	
	/**
	 * prints levelOrderTraversal of the binary tree.
	 */
	void levelOrderTraversal();

	/**
	 * @return the clone of the Binary Tree invoking this method
	 */
	BinaryTree<T> clone();
	
	/**
	 * It replaces the tree nodes with the new tree nodes whose pre-order
	 * traversal and in-order traversal array will be given.
	 * @param preOrder pre-order-traversal array
	 * @param inOrder in-order-traversal array
	 */
	void restore(T[] preOrder, T[] inOrder);
	
	/**
	 * @return the iterator for the Binary tree.
	 */
	Iterator<T> iterator();
}
