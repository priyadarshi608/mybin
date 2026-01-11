package av_binary;

public class SearchInBitonicArray {
	
	public static int searchInBitonicArray(int[] arr, int value) {
		int maxIndex = PeakElementInUnsortedArray.peakElementInUnsortedArray(arr);
		if (arr[maxIndex] == value) {
			return maxIndex;
		}
		if (maxIndex > 0) {
			int increasingArrIndex = binarySearchRecursive(arr, 0, maxIndex - 1, value);
			if (increasingArrIndex != -1) {
				return increasingArrIndex;
			}
		}
		if (maxIndex < arr.length - 1) {
			int decreasingArrIndex = binarySearchRecursiveInDSA(arr, maxIndex + 1, arr.length - 1, value);
			if (decreasingArrIndex != -1) {
				return decreasingArrIndex;
			}
		}
		return -1;
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

	private static int binarySearchRecursiveInDSA(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value) {
				return middle;
			} else if (arr[middle] < value) {
				return binarySearchRecursive(arr, low, middle - 1, value);
			} else {
				return binarySearchRecursive(arr, middle + 1, high, value);
			}
		}
		return -1;
	}
	
}
