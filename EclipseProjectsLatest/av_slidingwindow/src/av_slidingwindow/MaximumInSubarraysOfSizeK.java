package av_slidingwindow;

/************************************** TOUGH QUESTION **************************************/
/* USELESS ELEMENTS ARE REMOVED FROM BACK AND OUT OF WINDOW ELEMENTS ARE REMOVED FROM FRONT */
/************************************** TOUGH QUESTION **************************************/

// https://www.geeksforgeeks.org/sliding-window-maximum-maximum-of-all-subarrays-of-size-k/

import java.util.*;

public class MaximumInSubarraysOfSizeK {
	
	public static int[] maximumInSubarraysOfSizeK(int[] arr, int k) {
		int[] maxInSubarrays = new int[arr.length - k + 1];   // array to store the result
		int resultArrayIndex = 0;   // index of result array
		
		// Create a Double Ended Queue, Qi that will store indexes of array elements. The queue will store
		// indexes of USEFUL ELEMENTS in every window and it will maintain DECREASING ORDER of values from 
		// front to rear in Qi, i.e., arr[Qi.front[]] to arr[Qi.rear()] are sorted in decreasing order.
		Deque<Integer> Qi = new LinkedList<Integer>();
		
		// Process first k (or first window) elements of array
		for (int i = 0; i < k; ++i)	{
			// For every element, the previous smaller elements are useless so remove them from Qi
			while (!Qi.isEmpty() && arr[i] >= arr[Qi.peekLast()]) {
				Qi.removeLast();
			}
			// Add new element at rear of queue
			Qi.addLast(i);
		}

		// Process rest of the elements, i.e., from arr[k] to arr[n-1]
		for (int i = k; i < arr.length; ++i) {
			// The element at the front of the queue is the largest element of previous window
			maxInSubarrays[resultArrayIndex] = arr[Qi.peek()];
			resultArrayIndex++;

			// Remove the elements from FRONT which are out of this window
			while ((!Qi.isEmpty()) && Qi.peek() <= i - k) {
				Qi.removeFirst();
			}

			// Remove all elements smaller than the currently being added element
			// (remove useless elements)
			while ((!Qi.isEmpty()) && arr[i] >=	arr[Qi.peekLast()]) {
				Qi.removeLast();
			}

			// Add current element at the rear of Qi
			Qi.addLast(i);
		}

		// Print the maximum element of last window
		maxInSubarrays[resultArrayIndex] = arr[Qi.peek()];
		return maxInSubarrays;
	}

}
