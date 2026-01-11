package av_slidingwindow;

// https://www.geeksforgeeks.org/first-negative-integer-every-window-size-k/

import java.util.*;

public class FirstNegativeIntegerInWindowSizeK {
	
	public static void firstNegativeIntegerInWindowSizeK(int[] arr, int k) {
		Deque<Integer> deque = new LinkedList<>();
		
		for (int i = 0; i < k; i++) {
			if (arr[i] < 0) {
				deque.add(i);
			}
		}
		
		for (int i = k; i < arr.length; i++) {
			if (!deque.isEmpty()) {
				System.out.print(arr[deque.peek()] + " ");
			} else {
				System.out.print("0" + " ");
			}

			while ((!deque.isEmpty()) && deque.peek() < (i - k + 1)) {
				deque.remove(); // Remove from front of queue
			}

			// Add current element at the rear of deque if it is a negative integer
			if (arr[i] < 0) {
				deque.add(i);
			}
		}

		// Print the first negative integer of last window
		if (!deque.isEmpty())
			System.out.print(arr[deque.peek()] + " ");
		else
			System.out.print("0" + " ");	
	}

}
