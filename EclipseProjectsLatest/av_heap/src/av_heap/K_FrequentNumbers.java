package av_heap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class K_FrequentNumbers {
	
	public static int[] k_FrequentNumbers(int[] arr, int k) {
		int[] k_Frequent_Numbers = new int[k];
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < arr.length; i++) {
			if (!map.containsKey(arr[i])) {
				map.put(arr[i], 1);
			} else {
				int freq = map.get(arr[i]);
				map.put(arr[i], freq + 1);
			}
		}
		PriorityQueue<Pair> minHeap = new PriorityQueue<>();
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			Pair pair = new Pair(entry.getValue(), entry.getKey());
			minHeap.add(pair);
			if (minHeap.size() > k) {
				minHeap.poll();
			}
		}
		for (int i = 0; i < k; i++) {
			k_Frequent_Numbers[i] = minHeap.poll().b;
		}
		return k_Frequent_Numbers;
	}

}
