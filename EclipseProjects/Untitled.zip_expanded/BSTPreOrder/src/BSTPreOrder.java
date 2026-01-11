// Java program for an efficient solution to check if 
// a given array can represent Preorder traversal of 
// a Binary Search Tree 
import java.util.Stack; 

class BSTPreOrder { 

	boolean canRepresentBST(int pre[], int n) { 
		// Create an empty stack 
		Stack<Integer> s = new Stack<Integer>(); 

		// Initialize current root as minimum possible value 
		int root = Integer.MIN_VALUE; 

		// Traverse given array 
		for (int i = 0; i < n; i++) {
			// If we find a node who is on right side and smaller than root, return false
			if (pre[i] < root) { 
				return false; 
			} 

			// If pre[i] is in right subtree of stack top, Keep removing items 
			// smaller than pre[i] and make the last removed item as new root.
			while (!s.empty() && s.peek() < pre[i]) {
				root = s.pop();
			} 

			// At this point either stack is empty or pre[i] is smaller than root, push pre[i] 
			s.push(pre[i]); 
		} 
		return true; 
	} 

	public static void main(String args[]) { 
		BSTPreOrder bst = new BSTPreOrder(); 
		int[] pre1 = new int[]{40, 30, 35, 80, 100}; 
		int n = pre1.length; 
		if (bst.canRepresentBST(pre1, n) == true) { 
			System.out.println("true"); 
		} else { 
			System.out.println("false"); 
		}
		System.out.println();
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println();
		int[] pre2 = new int[]{40, 30, 35, 20, 80, 100}; 
		int n1 = pre2.length; 
		if (bst.canRepresentBST(pre2, n) == true) { 
			System.out.println("true"); 
		} else { 
			System.out.println("false"); 
		} 
	}
	
//	boolean canRepresentBST(int pre[], int n) { 
//		// Create an empty stack 
//		Stack<Integer> s = new Stack<Integer>(); 
//
//		// Initialize current root as minimum possible 
//		// value 
//		int root = Integer.MIN_VALUE; 
//
//		// Traverse given array 
//		for (int i = 0; i < n; i++) {
//			System.out.println("i : " + i);
//			System.out.println("pre[i] : " + pre[i]);
//			System.out.println("root : " + root);
//			printStack(s, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//			// If we find a node who is on right side 
//			// and smaller than root, return false 
//			if (pre[i] < root) { 
//				return false; 
//			} 
//
//			// If pre[i] is in right subtree of stack top, Keep removing items 
//			// smaller than pre[i] and make the last removed item as new root.
//			if (!s.empty()) {
//				System.out.println("s.peek() : " + s.peek());
//			}
//			while (!s.empty() && s.peek() < pre[i]) {
//				System.out.println("Inside while...");
//				root = s.pop();
//				System.out.println("root : " + root);
//				printStack(s, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//			} 
//
//			// At this point either stack is empty or 
//			// pre[i] is smaller than root, push pre[i] 
//			s.push(pre[i]); 
//			printStack(s, "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
//		} 
//		return true; 
//	} 
//
	static void printStack(Stack<Integer> s, String delimiter) {
		System.out.println(delimiter);
		Stack<Integer> scopy1 = new Stack<>(); 
		Stack<Integer> scopy2 = new Stack<>(); 
		while (!s.isEmpty()) {
			Integer sInt = s.pop();
//			System.out.print(sInt + " ");
			scopy1.push(sInt);
			scopy2.push(sInt);
		}
		while (!scopy2.isEmpty()) {
			Integer sInt = scopy2.pop();
			System.out.print(sInt + " ");
		}
		System.out.println();
		while (!scopy1.isEmpty()) {
			Integer scopyInt = scopy1.pop();
			s.push(scopyInt);
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
		System.out.println();
	}
	
} 
