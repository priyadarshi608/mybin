// Java program for iterative Tower of Hanoi 

public class TOH {
	
	// A structure to represent a stack 
	private static class Stack {
		int capacity; 
		int top; 
		int array[]; 
		
		// function to create a stack of given capacity. 
		Stack(int capacity) {
			this.capacity = capacity; 
			this.top = -1; 
			this.array = new int[capacity]; 
		}
		
		// Stack is full when the top is equal to the last index 
		boolean isFull() {
			return (this.top == this.capacity - 1); 
		} 
		
		// Stack is empty when top is equal to -1 
		boolean isEmpty() {
			return (this.top == -1); 
		}
		
		// Function to add an item to stack. It increases top by 1 
		void push(int item) {
			if(isFull())
				return;
			this.array[++this.top] = item; 
		}
		
		// Function to remove an item from stack. It decreases top by 1 
		int pop() {
			if(isEmpty()) 
				return Integer.MIN_VALUE; 
			return this.array[this.top--]; 
		}
		
	} 
	
	// Function to implement legal movement between two poles
	void moveDisksBetweenTwoPoles(Stack src, Stack dest, char s, char d) {
		int pole1TopDisk = src.pop();
		int pole2TopDisk = dest.pop();

		// When pole 1 is empty 
		if (pole1TopDisk == Integer.MIN_VALUE) {
			src.push(pole2TopDisk); 
			moveDisk(d, s, pole2TopDisk); 
		}
		// When pole2 pole is empty 
		else if (pole2TopDisk == Integer.MIN_VALUE) {
			dest.push(pole1TopDisk); 
			moveDisk(s, d, pole1TopDisk); 
		}
		// When top disk of pole1 > top disk of pole2 
		else if (pole1TopDisk > pole2TopDisk) {
			src.push(pole1TopDisk); 
			src.push(pole2TopDisk); 
			moveDisk(d, s, pole2TopDisk); 
		} 
		// When top disk of pole1 < top disk of pole2 
		else {
			dest.push(pole2TopDisk); 
			dest.push(pole1TopDisk); 
			moveDisk(s, d, pole1TopDisk); 
		}
	} 
	
	// Function to show the movement of disks 
	void moveDisk(char fromPeg, char toPeg, int disk) {
		System.out.println("Move the disk "+ disk + " from "+fromPeg+" to "+toPeg);
	}
	
	// Function to implement TOH puzzle 
	void tohIterative(int num_of_disks, Stack src, Stack aux, Stack dest) { 
		int i, total_num_of_moves; 
		char s = 'S', d = 'D', a = 'A'; 
	
		// If number of disks is even, then interchange destination pole and auxiliary pole 
		if (num_of_disks % 2 == 0) {
			char temp = d; 
			d = a; 
			a = temp; 
		} 
		total_num_of_moves = (int) (Math.pow(2, num_of_disks) - 1); 
	
		// Larger disks will be pushed first 
		for (i = num_of_disks; i >= 1; i--) 
			src.push(i); 
	
		for (i = 1; i <= total_num_of_moves; i++) {
			if (i % 3 == 1) 
			moveDisksBetweenTwoPoles(src, dest, s, d); 
	
			else if (i % 3 == 2) 
			moveDisksBetweenTwoPoles(src, aux, s, a); 
	
			else if (i % 3 == 0) 
			moveDisksBetweenTwoPoles(aux, dest, a, d); 
		} 
	} 
	
	// Driver Program to test above functions 
	public static void main(String[] args) {
		
		// Input: number of disks 
		int num_of_disks = 15; 
		
		TOH ob = new TOH(); 
		Stack src, dest, aux; 
		
		// Create three stacks of size 'num_of_disks' to hold the disks 
		src = new Stack(num_of_disks); 
		dest = new Stack(num_of_disks); 
		aux = new Stack(num_of_disks); 
		
		ob.tohIterative(num_of_disks, src, aux, dest); 
	} 
} 
