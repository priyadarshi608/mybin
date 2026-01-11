package av_binary;

public class SearchInSortedRotatedArray {
	
	public static int searchInSortedRotatedArray(int[] arr, int value) {
		int minimumElementIndex = RotationInSortedArray.rotationInSortedArray(arr);
		if (arr[minimumElementIndex] == value) {
			return minimumElementIndex;
		} else {
			int leftIndex = SearchInSortedRotatedArray.binarySearchRecursive(arr, 0, minimumElementIndex - 1, value);
			int rightIndex = SearchInSortedRotatedArray.binarySearchRecursive(arr, minimumElementIndex + 1, arr.length - 1, value);
			if (rightIndex != -1) {
				return rightIndex;
			} else if (leftIndex != -1) {
				return leftIndex;
			} else {
				return -1;
			}
		}
	}
	
	private static int binarySearchRecursive(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (arr[middle] > value) {
				return binarySearchRecursive(arr, low, middle - 1, value);
			} else {
				return binarySearchRecursive(arr, middle + 1, high, value);
			}
		}
		return -1;
	}

}
