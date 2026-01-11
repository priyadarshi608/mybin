import java.util.Stack;

public class BinaryTree<T> {
	
	public Node<T> root;
	
	public BinaryTree() {}
	
	/**
     * 1) Create an empty stack and initialize current node as root
	 * 2) Push root node to stack.
	 * 3) Do following while stack is not empty.
	 *     a) Pop an item from stack and print it.
	 *     b) Push right child of popped item to stack
	 *     c) Push left child of popped item to stack
	 */
    void iterativePreorder() {
    	Node<T> current = root;
        if (current == null) {
            return;
        } 
  
        Stack<Node<T>> nodeStack = new Stack<>(); 
        nodeStack.push(current);
  
        while (!nodeStack.empty()) {
            Node<T> mynode = nodeStack.pop(); 
            System.out.print(mynode.data + " "); 
  
            if (mynode.right != null) { 
                nodeStack.push(mynode.right); 
            } 
            if (mynode.left != null) { 
                nodeStack.push(mynode.left); 
            } 
        }
    }

    /**
     * 1) Create an empty stack and initialize current node as root
     * 2) while (current is not null OR stack is not empty)
     *     a) while (current is not null) => push current into stack and make current = current.left.
     *     b) current = stack.pop
     *     c) print current;
     *     d) current = current.right;
     * 3) If current is NULL and stack is empty then we are done.
     */
    void iterativeInorder() { 
    	Node<T> curr = root;
        if (curr == null) 
            return; 
  
        Stack<Node<T>> s = new Stack<>(); 
  
        while (curr != null || !s.isEmpty()) { 
            while (curr !=  null) {
                s.push(curr); 
                curr = curr.left; 
            } 
  
            curr = s.pop(); 
  
            System.out.print(curr.data + " "); 

            curr = curr.right; 
        } 
    }

    /**
     * 1) Create an empty stack and initialize current node as root
     * 2) Do following while current is not NULL
     *     a) Push current's right child and then current to stack.
     *     b) Set current as current's left child.
     * 3) Pop an item from stack and set it as current.
     *     a) If the popped item has a right child and the right child is at top of stack, then
     *        remove the right child from stack,
     *        push the current back 
     *        and set current as current's right child.
     *     b) Else then
     *        print current's data
     *        set current as NULL.
     * 4) Repeat steps 2 and 3 while stack is not empty.
     */
    void iterativePostorder() {
    	Node<T> current = root;
        if (current == null) 
            return; 
  
        Stack<Node<T>> S = new Stack<>();
        do {
        	while(current != null) {
        		if (current.right != null) {
        			S.push(current.right);
        		}
        		S.push(current);
        		current = current.left;
        	}
        	current = S.pop();
        	if (current.right != null && !S.isEmpty() && S.peek().equals(current.right)) {
        		S.pop();
        		S.push(current);
        		current = current.right;
        	} else {
                System.out.print(current.data + " "); 
                current = null;
			}
        } while (!S.isEmpty());
    }
        
}
