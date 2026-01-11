package av_binary;

public class FloorInSortedArray {
	
	public static int search(int[] arr, int value) {
		return FloorInSortedArray.searchRecursive(arr, 0, arr.length - 1, value);
	}
	
	private static int searchRecursive(int[] arr, int low, int high, int value) {
		if (low <= high) {
			if (arr[high] <= value) {
				return high;
			}
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (value < arr[middle] && middle > 0 && arr[middle - 1] <= value) {
				return middle - 1;
			} else if (arr[middle] > value) {
				return searchRecursive(arr, low, middle - 1, value);
			} else {
				return searchRecursive(arr, middle + 1, high, value);
			}
		}
		return -1;
	}

}
