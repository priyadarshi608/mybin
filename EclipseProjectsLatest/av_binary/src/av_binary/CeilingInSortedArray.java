package av_binary;

public class CeilingInSortedArray {

	public static int search(int[] arr, int value) {
		return CeilingInSortedArray.searchRecursive(arr, 0, arr.length - 1, value);
	}
	
	private static int searchRecursive(int[] arr, int low, int high, int value) {
		if (low <= high) {
			if (value <= arr[low]) {
				return low;
			}
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (arr[middle] < value && middle < arr.length - 1 && value <= arr[middle + 1]) {
				return middle + 1;
			} else if (arr[middle] > value) {
				return searchRecursive(arr, low, middle - 1, value);
			} else {
				return searchRecursive(arr, middle + 1, high, value);
			}
		}
		return -1;
	}
	
}
