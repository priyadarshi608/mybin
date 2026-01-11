package av_binary;

public class SearchInNearlySortedArray {
	
	public static int search(int[] arr, int value) {
		SearchInNearlySortedArray searchInNearlySortedArray = new SearchInNearlySortedArray();
		return searchInNearlySortedArray.binarySearchRecursive(arr, 0, arr.length - 1, value);
	}
	
	private int binarySearchRecursive(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (middle > 0 && arr[middle - 1] == value) {
				return middle - 1;
			} else if (middle < arr.length - 1 && arr[middle + 1] == value) {
				return middle + 1;
			} else if (arr[middle] > value) {
				return binarySearchRecursive(arr, low, middle - 2, value);
			} else {
				return binarySearchRecursive(arr, middle + 2, high, value);
			}
		}
		return -1;
	}

}
