
public class DoublyLinkedList {
	
	Node head, tail;
	
	private class Node {
		int val;
		Node next, prev;
		
		public Node(int val) {
			this.val = val;
		}
	}

	private void reverseDLL() {
		Node temp = null;
		Node curr = head;
		tail = head;
		
		while (curr != null) {
			temp = curr.prev;
			curr.prev = curr.next;
			curr.next = temp;
			curr = curr.prev;
		}

		// check for the cases like empty list and list with only one node,
		// MEANS IF temp IS NULL, WE DON'T NEED TO CHANGE THE head.
		if (temp != null) {
			head = temp.prev;
		}
	}
	
	private void reverseDLLRecursive() {
		tail = head;
		head = reverseDLLRecursiveUtil(head);
	}
	
	private Node reverseDLLRecursiveUtil(Node head) {
		
		// If empty list, return
		if (head == null) {
			return null;
		}
		
		// Otherwise, swap the next and prev
		Node temp = head.prev;
		head.prev = head.next;
		head.next = temp;
		
		// If the prev is now null, the list has been fully reversed
		if (head.prev == null) {
			return head;
		}
		
		return reverseDLLRecursiveUtil(head.prev);
	}
	
	private void groupReverseDLL(int k) {
		head = groupReverseDLLUtil(head, k);
		tail = head;
		if (tail != null && tail.next != null) {
			while (tail.next != null) {
				tail = tail.next;
			}
		}
	}
	
	private Node groupReverseDLLUtil(Node head, int k) {
		Node next = null;
		Node newHead = null;
		Node curr = head;
		int count = 0;
		
		while (count < k && curr != null) {
			next = curr.next;
			newHead = pushCurrAtHead(newHead, curr);  // pointers change of singlyLinkedList group reversal in included here.
			curr = next;
			count++;
		}
		
		if (next != null) {
			head.next = groupReverseDLLUtil(next, k);
			head.next.prev = head;
		}
		return newHead;
	}
	
	// Here curr is made the new HEAD
	private Node pushCurrAtHead(Node newHead, Node curr) {
		curr.prev = null;
		curr.next = newHead;
		if (newHead != null) {
			newHead.prev = curr;
		}
		newHead = curr;
		return newHead;
	}
	
	public static void main(String[] args) {
		DoublyLinkedList linkedList = new DoublyLinkedList();
		linkedList.createLinkedList();
		printDLLForward(linkedList.head);
		printDLLBackward(linkedList.tail);
		linkedList.reverseDLL();
		printDLLForward(linkedList.head);
		printDLLBackward(linkedList.tail);
		linkedList.reverseDLLRecursive();
		printDLLForward(linkedList.head);
		printDLLBackward(linkedList.tail);
		linkedList.groupReverseDLL(3);
		printDLLForward(linkedList.head);
		printDLLBackward(linkedList.tail);
	}

	private static void printDLLForward(Node node) {
		System.out.print("Forward : ");
		while (node != null) {
			System.out.print(node.val + " ");
			node = node.next;
		}
		System.out.println();
	}
	
	private static void printDLLBackward(Node node) {
		System.out.print("Backward : ");
		while (node != null) {
			System.out.print(node.val + " ");
			node = node.prev;
		}
		System.out.println();
	}
	
	private Node createLinkedList() {
		push(10);
		push(9);
		push(8);
		push(7);
		push(6);
		push(5);
		push(4);
		push(3);
		push(2);
		push(1);
		return head;
	}

	private void push(int n) {
		if (head == null) {
			Node node = new Node(n);
			head = tail = node;
		} else {
			Node node = new Node(n);
			node.next = head;
			head.prev = node;
			head = node;
		}
	}
	
}
