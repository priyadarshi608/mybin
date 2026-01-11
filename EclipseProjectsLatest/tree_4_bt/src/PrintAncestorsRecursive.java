import java.util.Stack;

public class PrintAncestorsRecursive {

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
	
	// Recursive Function to print all ancestors of a given key 
	static boolean printAncestors(Node root,int key) {

        if (root == null) {
            return false;
        }
  
        if (root.data == key) {
            return true;
        }
  
        /* If target is present in either left or right subtree
           of this node, then print this node */
        if (printAncestors(root.left, key) || printAncestors(root.right, key)) {
            System.out.print(root.data + " ");
            return true;
        }
  
        /* Else return false */
        return false;
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
