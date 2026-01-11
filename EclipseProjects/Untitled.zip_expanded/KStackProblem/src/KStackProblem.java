// Java program to demonstrate implementation of k stacks in a single 
// array in time and space efficient way 

public class KStackProblem {
	// A Java class to represent k stacks in a single array of size n 
	static class KStack 
	{ 
		int arr[]; // Array of size n to store actual content to be stored in stacks 
		int top[]; // Array of size k to store indexes of top elements of stacks 
		int next[]; // Array of size n to store next entry in all stacks 
					// and free list 
		int n, k; 
		int free; // To store beginning index of free list 

		//constructor to create k stacks in an array of size n 
		KStack(int k1, int n1) 
		{ 
			// Initialize n and k, and allocate memory for all arrays 
			k = k1; 
			n = n1; 
			arr = new int[n]; 
			top = new int[k]; 
			next = new int[n]; 

			// Initialize all stacks as empty 
			for (int i = 0; i < k; i++) 
				top[i] = -1; 

			// Initialize all spaces as free 
			free = 0; 
			for (int i = 0; i < n - 1; i++) 
				next[i] = i + 1; 
			next[n - 1] = -1; // -1 is used to indicate end of free list
			printArray(arr,  "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			printArray(top,  "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
			printArray(next, "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
		}

		// A utility function to check if there is space available 
		boolean isFull() {
			return (free == -1); 
		} 

		// To push an item in stack number 'sn' where sn is from 0 to k-1 
		void push(int item, int sn) {
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			printArray(arr,  "");
			printArray(top,  "");
			printArray(next, "");
			System.out.println("item : " + item);
			System.out.println("sn : " + sn);
			System.out.println("top[sn] : " + top[sn]);
			// Overflow check 
			if (isFull()) {
				System.out.println("Stack Overflow"); 
				return; 
			} 

			System.out.println("free : " + free);
			int i = free; // Store index of first free slot 
			System.out.println("i : " + i);

			// Update index of free slot to index of next slot in free list 
			free = next[i]; 
			System.out.println("free : " + free);

			System.out.println("sn : " + sn);
			System.out.println("top[sn] : " + top[sn]);
			// Update next of top and then top for stack number 'sn' 
			System.out.println("i : " + i);
			next[i] = top[sn]; 
			System.out.println("next[i] : " + next[i]);
			top[sn] = i;
			System.out.println("top[sn] : " + top[sn]);
			System.out.println("free : " + free);

			// Put the item in array 
			arr[i] = item;
			printArray(arr,  "");
			printArray(top,  "");
			printArray(next, "");
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} 

		// To pop an from stack number 'sn' where sn is from 0 to k-1 
		int pop(int sn) {
			System.out.println("------------------------------------------------------------------------");
			printArray(arr,  "");
			printArray(top,  "");
			printArray(next, "");
			System.out.println("sn : " + sn);
			System.out.println("top[sn] : " + top[sn]);
			System.out.println("free : " + free);
			// Underflow check 
			if (isEmpty(sn)) {
				System.out.println("Stack Underflow"); 
				return Integer.MAX_VALUE; 
			} 

            System.out.println("top[sn] : " + top[sn]);
			// Find index of top item in stack number 'sn' 
			int i = top[sn]; 
            System.out.println("i : " + i);

            System.out.println("next[i] : " + next[i]);
			top[sn] = next[i]; // Change top to store next of previous top 
            System.out.println("top[sn] : " + top[sn]);

            System.out.println("free : " + free);
			// Attach the previous top to the beginning of free list 
			next[i] = free; 
            System.out.println("next[i] : " + next[i]);
            System.out.println("i : " + i);
			free = i; 
            System.out.println("free : " + free);

			printArray(arr,  "");
			printArray(top,  "");
			printArray(next, "");
			System.out.println("------------------------------------------------------------------------");
			System.out.println("------------------------------------------------------------------------");
			// Return the previous top item 
			return arr[i]; 
		} 

		// To check whether stack number 'sn' is empty or not 
		boolean isEmpty(int sn) {
			return (top[sn] == -1); 
		} 

	} 

	// Driver program 
	public static void main(String[] args) {
		// Let us create 3 stacks in an array of size 10 
		int k = 3, n = 10; 
		
		KStack ks = new KStack(k, n); 

		ks.push(15, 2); 
		ks.push(45, 2); 

		// Let us put some items in stack number 1 
		ks.push(17, 1); 
		ks.push(49, 1); 
		ks.push(39, 1); 

		// Let us put some items in stack number 0 
		ks.push(11, 0); 
		ks.push(9, 0); 
		ks.push(7, 0); 

		System.out.println("Popped element from stack 2 is " + ks.pop(2)); 
		System.out.println("Popped element from stack 2 is " + ks.pop(2)); 
		System.out.println("Popped element from stack 2 is " + ks.pop(2)); 
		System.out.println("Popped element from stack 1 is " + ks.pop(1)); 
		System.out.println("Popped element from stack 1 is " + ks.pop(1)); 
		System.out.println("Popped element from stack 1 is " + ks.pop(1)); 
		System.out.println("Popped element from stack 0 is " + ks.pop(0)); 
	}
	
	static void printArray(int[] arr, String delimiter) {
		if (!delimiter.equals(""))
			System.out.println(delimiter);
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
		if (!delimiter.equals(""))
			System.out.println(delimiter);
	}
	
} 
