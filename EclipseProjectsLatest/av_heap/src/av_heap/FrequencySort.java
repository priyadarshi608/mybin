package av_heap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class FrequencySort {
	
	public static int[] frequencySort(int[] arr) {
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < arr.length; i++) {
			if (!map.containsKey(arr[i])) {
				map.put(arr[i], 1);
			} else {
				int freq = map.get(arr[i]);
				map.put(arr[i], freq + 1);
			}
		}
		// The Collections.reverseOrder() provides a Comparator that would sort the 
		// elements in the PriorityQueue in a the opposite order to their natural 
		// order in this case.
		PriorityQueue<Pair> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			Pair pair = new Pair(entry.getValue(), entry.getKey());
			maxHeap.add(pair);
		}
		int arrIndex = 0;
		for (int i = 0; i < map.size(); i++) {    // Don't think of using maxHeap.size() here as we are polling it inside the loop.
			Pair pair = maxHeap.poll();
			for (int j = 0; j < pair.a; j++) {
				arr[arrIndex] = pair.b;
				arrIndex++;
			}
		}
		return arr;
	}

}
