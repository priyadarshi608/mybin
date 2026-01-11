package sss.bst.binary_tree;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * The concrete BST implementation class of Binary Tree interface.
 * 
 * @author shubham
 * @param <T> the data type being stored in the nodes of the tree
 */
public class BinarySearchTree<T extends Comparable<T>> implements BinaryTree<T>,
                                Iterable<T>{
	
	private Node<T> root; // Root of the tree
	private Comparator<T> comparator; // Comparator to compare the elements.
	
	public BinarySearchTree() {
	}
	
	public BinarySearchTree(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	// compares the data of two nodes.
	private int compare(T t1, T t2) {
		if (comparator == null)
			return t1.compareTo(t2);
		else
			return comparator.compare(t1, t2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void insert(T data) {
		root = insert(root, data);
	}

	private Node<T> insert(Node<T> node, T data) {
		if (node == null)
			return new Node<T>(data);
		
		int compareResult = compare(node.data, data);
		
		if (compareResult == 0)
			return node;
		
		if (compareResult < 0)
			node.right = insert(node.right, data);
		else
			node.left = insert(node.left, data);
		
		return node;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void delete(T toDelete) {
		root = delete(root, toDelete);
	}
	
	private Node<T> delete(Node<T> node, T toDelete) {
		if (node == null)
			throw new NoSuchElementException();
		
		int compareResult = compare(node.data, toDelete);
		
		if (compareResult < 0)
			node.right = delete(node.right, toDelete);
		else if (compareResult > 0)
			node.left = delete(node.left, toDelete);
		else {
			if (node.left == null)
				return node.right;
			else if (node.right == null)
				return node.left;
			else {
				node.data = inorderPredecessor(node.left);
				node.left = delete(node.left, node.data);
			}
		}
		
		return node;
	}

	private T inorderPredecessor(Node<T> node) {
		while (node.right != null)
			node = node.right;
		return node.data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public	boolean search(T toSearch) {
		return search(root, toSearch);
	}
	
	private boolean search(Node<T> node, T toSearch) {
		if (node == null)
			return false;
		
		int compareResult = compare(node.data, toSearch);
		
		if (compareResult == 0)
			return true;
		else if (compareResult < 0)
			return search(node.left, toSearch);
		else
			return search(node.right, toSearch);
	}

	/**
	 * {@inheritDoc}
	 */
	public	void preOrderTraversal() {
		preOrderTraversal(root);
	}

	private void preOrderTraversal(Node<T> node) {
		if (node != null) {
			System.out.print(node + " ");
			preOrderTraversal(node.left);
			preOrderTraversal(node.right);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public	void inOrderTraversal() {
		inOrderTraversal(root);
	}

	private void inOrderTraversal(Node<T> node) {
		if (node != null) {
			inOrderTraversal(node.left);
			System.out.print(node + " ");
			inOrderTraversal(node.right);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public	void postOrderTraversal() {
		postOrderTraversal(root);
	}

	private void postOrderTraversal(Node<T> node) {
		if (node != null) {
			postOrderTraversal(node.left);
			postOrderTraversal(node.right);
			System.out.print(node + " ");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public	String toString() {
		StringBuffer bstString = new StringBuffer();
		for (T x : this)
			bstString = bstString.append(x + " ");
		
		return bstString.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public	int height() {
		int height = height(root);
		return (height < 0) ? 0 : height;
	}

	private int height(Node<T> node) {
		if (node == null)
			return -1;
		else
			return 1 + Math.max(height(node.left), height(node.right));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public	int countLeaves() {
		return countLeaves(root);
	}
	
	private int countLeaves(Node<T> node) {
		if (node == null)
			return 0;
		else if (node.left == null && node.right == null)
			return 1;
		else
			return countLeaves(node.left) + countLeaves(node.right);
	}

	/**
	 * {@inheritDoc}
	 */
	public	int width() {
		int max = 0;
		for (int i = 0; i <= height(); i++) {
			int temp = width(root, i);
			if (temp > max)
				max = temp;
		}
		return max;
	}
	
	private int width(Node<T> node, int depth) {
		if (node == null)
			return 0;
		if (depth == 0)
			return 1;
		else
			return width(node.left, depth - 1) + width(node.right, depth - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	public	int diameter() {
		return diameter(root);
	}
	
	private int diameter(Node<T> node) {
		if (node == null)
			return 0;

		int nodeIncluded = height(node.left) + height(node.right) + 3;
		int nodeNotincluded = Math.max(diameter(node.left), diameter(node.right));
		
		return Math.max(nodeIncluded, nodeNotincluded);
	}

	/**
	 * {@inheritDoc}
	 */
	public	BinaryTree<T> clone() {
		BinarySearchTree<T> bst;
		
		if (this.comparator == null)
			bst = new BinarySearchTree<T>();
		else
			bst = new BinarySearchTree<T>(comparator);
		
		bst.root = clone(root);
		
		return bst;
	}
	
	private Node<T> clone(Node<T> node) {
		if (node == null)
			return node;
		else
			return new Node<T>(node.data, clone(node.left), clone(node.right));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void restore(T[] pre, T[] in) {
		root = restore(pre, 0, pre.length - 1, in, 0, in.length - 1);
	}

	private Node<T> restore(T[] pre, int preL, int preR, T[] in, int inL, int inR) {
		if (preL <= preR) {
			int count = 0;
			while (!(pre[preL] == in[inL + count]))
				count++;
			
			Node<T> temp = new Node<T>(pre[preL]);
			temp.left = restore(pre, preL + 1, preL + count, in, inL, inL + count - 1);
			temp.right = restore(pre, preL + count + 1, preR, in, inL + count + 1, inR);
			return temp;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public	Iterator<T> iterator() {
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<T> {
		Stack<Node<T>> stack = new Stack<Node<T>>();
		
		public MyIterator() {
			if (root != null)
				stack.push(root);
		}

		public boolean hasNext(){
			return (!stack.empty());
		}

		// Gives the nodes in inorder
		public T next() {
			Node<T> current = stack.peek();
			if (current.left != null)
				stack.push(current.left);
			else {
				Node<T> temp = stack.pop();
				while (temp.right == null) {
					if (stack.isEmpty())
						return current.data;
					temp = stack.pop();
				}
				stack.push(temp.right);
			}
			return current.data;
		}
	}
	
	// Generic node class of the Binary tree.
	private static class Node<T> {
		T data;
		Node<T> left;
		Node<T> right;
		
		public Node(T data) {
			this.data = data;
			this.left = null;
			this.right = null;
		}
		
		public Node(T data, Node<T> left, Node<T> right) {
			this.data = data;
			this.left = left;
			this.right = right;
		}
		
		public String toString() {
			return data.toString();
		}
	}
}
