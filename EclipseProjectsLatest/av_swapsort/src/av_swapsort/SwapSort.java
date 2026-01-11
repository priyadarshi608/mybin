package av_swapsort;

import java.util.*;

/***
 * 
Find Missing and Duplicate Number in an Array:
Given an unsorted array of size n which has numbers from 1 to n but some numbers are missing or in their place another numbers are present. You have to find missing and duplicate numbers.
Input: 2, 3, 1, 4, 2, 6, 3, 8,
Approach 1: sort the array and traverse (time - O(nlogn), space - O(1)),
Approach 2: use map to check count (time - O(n), space - O(n)), don't try to sort the keys of map (it will take O(nlogn)). check numbers from 1 to n instead,
Approach 3: swap sort (time - O(n), space - O(1), - this sorting is possible in this time complexity because here we know that the numbers are 1 to n only in the size of array n),
 *
 ***/

public class SwapSort {
	
	static class Pair {
		public int missing;
		public int duplicate;
		public Pair(int missing, int duplicate) {
			super();
			this.missing = missing;
			this.duplicate = duplicate;
		}
	}

	public static void main(String[] args) {
		int[] arr = new int[] {2, 3, 1, 4, 2, 6, 3, 8};
		swapSort(arr);
		List<Pair> pairs = findMissingAndDuplicatePairs(arr);
		for (Pair pair : pairs) {
			System.out.println("Missing: " + pair.missing + ", Duplicate: " + pair.duplicate);
		}
	}
	
	private static List<Pair> findMissingAndDuplicatePairs(int[] arr) {
		List<Pair> pairs = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != i + 1) {
				Pair pair = new Pair(i + 1, arr[i]);
				pairs.add(pair);
			}
		}
		return pairs;
	}
	
	private static void swapSort(int[] arr) {
		int i = 0;
		while (i < arr.length) {
			if (arr[i] != arr[arr[i] - 1]) {
				swap(arr, i, arr[i] - 1);
			} else {
				i++;
			}
		}
	}
	
	private static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
}
