package av_heap;

// https://www.geeksforgeeks.org/sum-elements-k1th-k2th-smallest-elements/

public class SumBetweenK1SmallestAndK2Smallest {
	
	public static int sumBetweenK1SmallestAndK2Smallest(int[] arr, int k1, int k2) {
		int sum = 0;
		int k1th = KthSmallestElement.kthSmallestElement(arr, k1);
		int k2th = KthSmallestElement.kthSmallestElement(arr, k2);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > k1th && arr[i] < k2th) {
				sum = sum + arr[i];
			}
		}
		return sum;
	}

}
