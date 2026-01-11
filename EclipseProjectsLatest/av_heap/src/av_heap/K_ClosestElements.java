package av_heap;

import java.util.Collections;
import java.util.PriorityQueue;

public class K_ClosestElements {
	
	/*******************************************************************************************************/
	// If internal class is created and accessed from within static method, then class should also be static.
	/*******************************************************************************************************/
//	static class Pair implements Comparable<Pair>{
//		
//		public int a;
//		public int b;
//		
//		Pair(int a, int b) {
//			super();
//			this.a = a;
//			this.b = b;
//		}
//
//		@Override
//		public int compareTo(Pair anotherPair) {
//			return this.a - anotherPair.a;
//		}
//
//	}
//	
	public static int[] k_ClosestElements(int[] arr, int k, int x) {
		int[] k_ClosestElements = new int[k];
		// The Collections.reverseOrder() provides a Comparator that would sort the 
		// elements in the PriorityQueue in a the opposite order to their natural 
		// order in this case.
		PriorityQueue<Pair> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (int i = 0; i < arr.length; i++) {
			Pair pair = new Pair(Math.abs(x - arr[i]), arr[i]);
			maxHeap.add(pair);
			// Because here the size of priority queue is never more than k, so time complexity of overall
			// code is also not more than (n log k).
			if (maxHeap.size() > k) {
				maxHeap.poll();
			}
		}
		for (int i = 0; i < k; i++) {
			k_ClosestElements[i] = maxHeap.poll().b;
		}
		return k_ClosestElements;
	}

}
