package av_binary;

public class MinimumDifferenceElementInASortedArray {
	
	public static int minimumDifferenceElementInASortedArray(int[] arr, int value) {
		if (value <= arr[0]) {
			return 0;
		}
		if (value >= arr[arr.length - 1]) {
			return arr.length - 1;
		}
		return searchRecursive(arr, 0, arr.length, value);
	}
	
	private static int searchRecursive(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (arr[middle] > value) {
				return searchRecursive(arr, low, middle - 1, value);
			} else {
				return searchRecursive(arr, middle + 1, high, value);
			}
		} else {
			// We don't need to check whether low is within valid limits or not because 
			// we have already checked boundary conditions before recursion. low or high
			// will always be > 0 and < arr.length - 1;
			int lowDiff = Math.abs(arr[low] - value);
			int highDiff = Math.abs(arr[high] - value);
			if (lowDiff <= highDiff) {
				return low;
			} else {
				return high;
			}
		}
	}

}
