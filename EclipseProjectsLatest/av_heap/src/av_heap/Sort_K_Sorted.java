package av_heap;

import java.util.PriorityQueue;

public class Sort_K_Sorted {
	
	public static int[] sort_K_Sorted(int[] arr, int k) {
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		int sortedIndex = 0;
		for (int i = 0; i < arr.length; i++) {
			minHeap.add(arr[i]);
			// Because here the size of priority queue is never more than k, so time complexity of overall
			// code is also not more than (n log k).
			if (minHeap.size() > k) {
				arr[sortedIndex] = minHeap.poll();
				sortedIndex++;
			}
		}
		for (int i = 0; i < k; i++) {
			arr[sortedIndex] = minHeap.poll();
			sortedIndex++;
		}
		return arr;
	}

}
