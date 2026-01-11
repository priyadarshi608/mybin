import javax.xml.soap.Node;

public class MergeSortDLL { 
	node head = null; 
	node tail = null; 
	// node a, b; 
	static class node { 
		int val; 
		node next, prev;

		public node(int val) 
		{ 
			this.val = val; 
		} 
	} 

	node sortedMerge(node a, node b) {
		node result = null; 
		/* Base cases */
		if (a == null) {
			node bTemp = b;
			while (bTemp != null) {
				tail = bTemp;
				bTemp = bTemp.next;
			}
			return b;
		}
		if (b == null) {
			node aTemp = a;
			while (aTemp != null) {
				tail = aTemp;
				aTemp = aTemp.next;
			}
			return a;
		}

		node temp = null;
		/* Pick either a or b, and recur */
		if (a.val <= b.val) { 
			result = a; 
//			result.next = sortedMerge(a.next, b); 
			temp = sortedMerge(a.next, b);
			result.next = temp;
			temp.prev = result;
		} else {
			result = b; 
//			result.next = sortedMerge(a, b.next); 
			temp = sortedMerge(a, b.next); 
			result.next = temp;
			temp.prev = result;
		}
		while (temp != null) {
			tail = temp;
			temp = temp.next;
		}
		return result; 
	} 

	node mergeSort(node h) {
		// Base case : if head is null 
		if (h == null || h.next == null) { 
			return h; 
		}

		// get the middle of the list 
		node middle = getMiddle(h);
		node nextofmiddle = middle.next;

		// set the next of middle node to null 
		middle.next = null;
		nextofmiddle.prev = null;

		// Apply mergeSort on left list 
		node left = mergeSort(h);

		// Apply mergeSort on right list 
		node right = mergeSort(nextofmiddle);

		// Merge the left and right lists 
		node sortedlist = sortedMerge(left, right);
		return sortedlist;
	}

	// Utility function to get the middle of the linked list 
	node getMiddle(node h) 
	{ 
		// Base case 
		if (h == null) 
			return h; 
		node fastptr = h.next; 
		node slowptr = h; 

		// Move fastptr by two and slow ptr by one 
		// Finally slowptr will point to middle node 
		while (fastptr != null) { 
			fastptr = fastptr.next; 
			if (fastptr != null) { 
				slowptr = slowptr.next; 
				fastptr = fastptr.next; 
			} 
		} 
		return slowptr; 
	} 

	void push(int new_data) {
		node new_node = new node(new_data); 
		if (head == null) {
			new_node.prev = null;
			new_node.next = null;
			head = new_node;
			tail = new_node;
		} else {
			/* link the old list off the new node */
			new_node.next = head;
			head.prev = new_node;

			/* move the head to point to the new node */
			head = new_node; 
		}
	}
	
	// Utility function to print the linked list 
	void printListForward(node headref, String message) {
		System.out.print(message);
		while (headref != null) { 
			System.out.print(headref.val + " "); 
			headref = headref.next; 
		} 
		System.out.println();
	} 

	// Utility function to print the linked list 
	void printListBackward(node headref, String message) {
		System.out.print(message);
		while (headref != null) { 
			System.out.print(headref.val + " "); 
			headref = headref.prev;
		} 
		System.out.println();
	} 

	public static void main(String[] args) {
		MergeSortDLL li = new MergeSortDLL(); 

//		li.push(1);
//		li.push(15);
//		li.push(2);
		li.push(1);
		li.push(15); 
		li.push(10); 
		li.push(5); 
		li.push(20); 
		li.push(3); 
		li.push(2); 
		li.printListForward(li.head, "Printing list forward: ");
		li.printListBackward(li.tail, "Printing list backward: ");

		// Apply merge Sort 
		li.head = li.mergeSort(li.head);
		System.out.println("tail: " + li.tail.val);
		li.printListForward(li.head, "Printing list forward: ");
		li.printListBackward(li.tail, "Printing list backward: ");
	} 
} 
