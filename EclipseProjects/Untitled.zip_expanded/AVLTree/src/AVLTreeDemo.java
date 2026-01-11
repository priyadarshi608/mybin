import sss.binarytree.avl.AVLTree;

public class AVLTreeDemo {
	public static void main(String[] args) {
		Integer[] arr = {96, 85, 110, 64, 90};
		AVLTree<Integer> avlTree = new AVLTree<>();
		for(Integer n : arr) {
			avlTree.insert(n);
		}
		avlTree.printInTreeFormat();
		avlTree.insert(36);
		avlTree.printInTreeFormat();
		System.out.println("===================================================================");
		System.out.println();
		
		Integer[] arr1 = {46, 20, 54, 18, 23, 60, 7, 24};
		avlTree = new AVLTree<>();
		for(Integer n : arr1) {
			avlTree.insert(n);
		}
		avlTree.printInTreeFormat();
		avlTree.delete(60);
		avlTree.printInTreeFormat();
		System.out.println("===================================================================");
		System.out.println();
	}
}
