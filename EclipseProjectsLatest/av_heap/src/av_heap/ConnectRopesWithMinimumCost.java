package av_heap;

import java.util.PriorityQueue;

// https://www.geeksforgeeks.org/connect-n-ropes-minimum-cost/

public class ConnectRopesWithMinimumCost {
	
	public static int connectRopesWithMinimumCost(int[] arr) {
		int cost = 0;
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		for (int i = 0; i < arr.length; i++) {
			minHeap.add(arr[i]);
		}
		while(minHeap.size() >= 2) {
			int first = minHeap.poll();
			int second = minHeap.poll();
			cost = cost + (first + second);
			if (minHeap.size() > 0) {
				minHeap.add(first + second);
			}
		}
		return cost;
	}

}
