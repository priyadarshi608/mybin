package av_binary;

// https://www.geeksforgeeks.org/find-a-peak-in-a-given-array/
// Watch video as well on the above page for explanation.

public class PeakElementInUnsortedArray {
	
	public static int peakElementInUnsortedArray(int[] arr) {
		return searchRecursive(arr, 0, arr.length - 1);
	}
	
	private static int searchRecursive(int[] arr, int low, int high) {
		// We don't need to check (low <= high) as an array will always
		// have a peak element.
		
		int middle = low + (high - low) / 2;
		
		// Compare middle element with its neighbours (if neighbours exist)
		if ((middle == 0 || arr[middle - 1] <= arr[middle]) && (middle == arr.length - 1 || arr[middle + 1] <= arr[middle])) {
			return middle;
		} else if (middle > 0 && arr[middle - 1] > arr[middle]) {
			return searchRecursive(arr, low, middle - 1);
		} else {
			return searchRecursive(arr, middle + 1, high);
		}
	}

}
