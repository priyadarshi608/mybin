package av_heap;

import java.util.PriorityQueue;

public class K_LargestElements {
	
	public static int[] k_LargestElement(int[] arr, int k) {
		if (k > arr.length) {
			return new int[0];
		}
		
		int[] largestelement = new int[k];
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		for (int i = 0; i < arr.length; i++) {
			minHeap.add(arr[i]);
			// Because here the size of priority queue is never more than k, so time complexity of overall
			// code is also not more than (n log k).
			if (minHeap.size() > k) {
				minHeap.poll();
			}
		}
		for (int i = 0; i < k; i++) {
			largestelement[i] = minHeap.poll();
		}
		return largestelement;
	}

}
