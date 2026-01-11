
public class Demo {
	public static void main(String[] args) {
		BinaryTree<String> tree = new BinaryTree<String>(); 
		tree.root = new Node<String>("A");
		tree.root.left = new Node<String>("B");
		tree.root.left.left = new Node<String>("D");
		tree.root.left.right = new Node<String>("E");
		tree.root.left.right.left = new Node<String>("F");
		tree.root.right = new Node<String>("C");
		tree.root.right.left = new Node<String>("G");
		tree.root.right.right = new Node<String>("H");
		tree.root.right.right.right = new Node<String>("K");
		tree.root.right.right.left = new Node<String>("J");
		tree.root.right.right.left.left = new Node<String>("L");

//		BinaryTree<String> tree = new BinaryTree<String>(); 
//		tree.root = new Node<String>("1");
//		tree.root.left = new Node<String>("2");
//		tree.root.left.left = new Node<String>("4");
//		tree.root.left.right = new Node<String>("5");
//		tree.root.right = new Node<String>("3");
//		tree.root.right.left = new Node<String>("6");
//		tree.root.right.right = new Node<String>("7");
//		
		System.out.print("Preorder traversal:  "); 
		tree.iterativePreorder();
		System.out.println();

		System.out.print("Inorder traversal:   "); 
		tree.iterativeInorder();
		System.out.println();

		System.out.print("Postorder traversal: "); 
		tree.iterativePostorder();
	}
}
