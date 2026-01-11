package av_heap;

import java.util.Collections;
import java.util.PriorityQueue;

/*
 * Heap is generally preferred for priority queue implementation because heaps provide better performance 
 * compared arrays or linked list.
 *     In a Binary Heap, getHighestPriority() can be implemented in O(1) time, insert() can be implemented in 
 * O(Logn) time and deleteHighestPriority() can also be implemented in O(Logn) time.
 *     With Fibonacci heap, insert() and getHighestPriority() can be implemented in O(1) amortized time and 
 * deleteHighestPriority() can be implemented in O(Logn) amortized time.
 * 
 * */

public class KthSmallestElement {
	
	public static int kthSmallestElement(int[] arr, int k) {
		
		// PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		
		// The Collections.reverseOrder() provides a Comparator that would sort the 
		// elements in the PriorityQueue in a the opposite order to their natural 
		// order in this case.
		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (int i = 0; i < arr.length; i++) {
			maxHeap.add(arr[i]);
			// Because here the size of priority queue is never more than k, so time complexity of overall
			// code is also not more than (n log k).
			if (maxHeap.size() > k) {
				maxHeap.poll();
			}
		}
		return maxHeap.peek();
	}

}
