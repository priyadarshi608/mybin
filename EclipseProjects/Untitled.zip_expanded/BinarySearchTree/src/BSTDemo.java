import sss.bst.binary_tree.BinarySearchTree;

public class BSTDemo {
	public static void main(String args[]) {
	      Integer[] a = {1, 5, 2, 4, 7, 6, 9};
	      BinarySearchTree<Integer> bst = new BinarySearchTree<Integer>();
//	      MyComp myComp = new MyComp();
//	      BinarySearchTree<Integer> bst = new BinarySearchTree<Integer>(myComp);
	      for(Integer n : a)
	    	  bst.insert(n);

	      bst.printInTreeFormat();
	      bst.levelOrderTraversal();
	      System.out.println();
	      
	      bst.preOrderTraversal();
	      bst.inOrderTraversal();
	      bst.postOrderTraversal();
	      System.out.println();
	      
	      System.out.println(bst.toString());
	      System.out.println("width: " + bst.width());
	      System.out.println("height: " + bst.height());
	      System.out.println("diameter: " + bst.diameter());
	      System.out.println();
	      
	      bst.preOrderTraversal();
	      System.out.println();
	      System.out.println("Height of the tree: " + bst.height()); 
	      
	      //testing comparator
	      //build a mirror BinarySearchTree with a rule:  Left > Parent > Right
	      //code for the comparator at the bottom of the file
	      bst = new BinarySearchTree<Integer>(new MyComp());
	      for(Integer n : a)
	    	  bst.insert(n);

	      System.out.println("222222222222222222222222222");
	      bst.preOrderTraversal();
	      System.out.println();
	      System.out.println("333333333333333333333333333");
	      bst.inOrderTraversal();
	      System.out.println();


	      System.out.println("444444444444444444444444444");
	      for(Integer n : bst) System.out.print(n);
	      System.out.println();

	      System.out.println("555555555555555555555555555");
	      System.out.println(bst);

	      //testing restoring a tree from two given traversals
	      bst.restore(new Integer[] {11,8,6,4,7,10,19,43,31,29,37,49},
	                      new Integer[] {4,6,7,8,10,11,19,29,31,37,43,49});
	      System.out.println("666666666666666666666666666");
	      bst.preOrderTraversal();
	      System.out.println();
	      System.out.println("Number of leaves: " + bst.countLeaves());
	      System.out.println();
	      System.out.println("***************************");
	      for(Integer n : bst) System.out.print(n + " ");
	      System.out.println();
	      System.out.println("777777777777777777777777777");
	      bst.inOrderTraversal();
	      System.out.println();

	      //testing diameter
	      System.out.println("diameter = " + bst.diameter());
	      //testing width
	      System.out.println("width = " + bst.width());
	      System.out.println("height = " + bst.height());
	}
}
