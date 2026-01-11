// Java program to print all ancestors of a given key 
import java.util.Stack; 

public class PrintAncestorsIterative {
	// Class for a tree node 
	static class Node {
		int data; 
		Node left,right; 
		
		// constructor to create Node 
		// left and right are by default null 
		Node(int data) {
			this.data = data; 
		} 
	} 
	
	// Iterative Function to print all ancestors of a given key 
	static void printAncestors(Node root,int key) {
		Node curr = root;
		if(curr == null) 
			return; 
		
		// Create a stack to hold ancestors 
		Stack<Node> s = new Stack<>(); 
		
		// Traverse the complete tree in postorder way till we find the key 
		while(true) {
			// Traverse the left side. While traversing, push the nodes into 
			// the stack so that their right subtrees can be traversed later 
			while(curr != null && curr.data != key) {
				s.push(curr); // push current node 
				curr = curr.left; // move to next node 
			}

			// If the node whose ancestors are to be printed is found, 
			// then break the while loop. 
			if(curr != null && curr.data == key) {
				break;
			}
			
			// Check if right sub-tree exists for the node at top 
			// If not then pop that node because we don't need this 
			// node any more. 
			if(s.peek().right == null) {
				curr =s.pop();
				
				// If the popped node is right child of top, then remove the top 
				// as well. Left child of the top must have processed before. 
				while( s.empty() == false && s.peek().right == curr) {
					curr = s.pop();
				}
			}

			// if stack is not empty then simply set the curr as right child 
			// of top and start traversing right sub-tree. 
			curr = s.empty() ? null : s.peek().right; 
		} 
		
		// If stack is not empty, print contents of stack 
		// Here assumption is that the key is there in tree 
		while( !s.empty() ) {
			System.out.print(s.peek().data+" "); 
			s.pop(); 
		} 
	}
	
	// Driver program to test above functions 
	public static void main(String[] args) 
	{ 
		// Let us construct a binary tree 
		Node root = new Node(1); 
		root.left = new Node(2); 
		root.right = new Node(3); 
		root.left.left = new Node(4); 
		root.left.right = new Node(5); 
		root.left.left.left = new Node(7);
		root.left.right.left = new Node(6); 
		root.left.right.right = new Node(8);
		
		printAncestors(root, 6);
	} 

//	// Iterative Function to print all ancestors of a given key 
//	static void printAncestors(Node root,int key) {
//		if(root == null) 
//			return; 
//		
//		// Create a stack to hold ancestors 
//		Stack<Node> st = new Stack<>(); 
//		
//		// Traverse the complete tree in postorder way till we find the key 
//		while(true) {
//			printStack(st, "0000000000000000000000000000000000000000000000000000000", root);
//			
//			// Traverse the left side. While traversing, push the nodes into 
//			// the stack so that their right subtrees can be traversed later 
//			while(root != null && root.data != key) {
//				st.push(root); // push current node 
//				root = root.left; // move to next node 
//			}
//			printStack(st, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", root);
//			
//			// If the node whose ancestors are to be printed is found, 
//			// then break the while loop. 
//			if(root != null && root.data == key) {
//				break;
//			}
//			
//			// Check if right sub-tree exists for the node at top 
//			// If not then pop that node because we don't need this 
//			// node any more. 
//			if(st.peek().right == null) {
//				root =st.pop();
//				printStack(st, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", root);
//				
//				// If the popped node is right child of top, then remove the top 
//				// as well. Left child of the top must have processed before. 
//				while( st.empty() == false && st.peek().right == root) {
//					root = st.pop();
//					printStack(st, "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc", root);
//				}
//			}
//
//			printPeek(st);
//			// if stack is not empty then simply set the root as right child 
//			// of top and start traversing right sub-tree. 
//			root = st.empty() ? null : st.peek().right; 
//		} 
//		
//		// If stack is not empty, print contents of stack 
//		// Here assumption is that the key is there in tree 
//		while( !st.empty() ) {
//			System.out.print(st.peek().data+" "); 
//			st.pop(); 
//		} 
//	}
//	
	static void printStack(Stack<Node> s, String delimiter, Node root) {
		System.out.println(delimiter);
		if (root != null) {
			System.out.println("root : " + root.data);
		}
		else {
			System.out.println("root : null");
		}
		Stack<Node> scopy1 = new Stack<>(); 
		Stack<Node> scopy2 = new Stack<>(); 
		while (!s.isEmpty()) {
			Node sNode = s.pop();
//			System.out.print(sInt + " ");
			scopy1.push(sNode);
			scopy2.push(sNode);
		}
		while (!scopy2.isEmpty()) {
			Node sNode = scopy2.pop();
			System.out.print(sNode.data + " ");
		}
		System.out.println();
		while (!scopy1.isEmpty()) {
			Node scopyInt = scopy1.pop();
			s.push(scopyInt);
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
		System.out.println();
	}
	
	static void printPeek(Stack<Node> st) {
		if (!st.empty()) {
			System.out.println("st.peek() : " + st.peek().data);
			if (st.peek().right != null) {
				System.out.println("st.peek().right : " + st.peek().right.data);
			}
			else {
				System.out.println("st.peek().right is null.");
			}
		} else {
			System.out.println("st.empty() is true..");
		}
	}
	
} 
