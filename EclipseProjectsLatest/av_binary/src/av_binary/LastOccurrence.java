package av_binary;

public class LastOccurrence {

	public static int search(int[] arr, int value) {
		LastOccurrence lastOccurrence = new LastOccurrence();
		return lastOccurrence.binarySearchRecursiveLastOccurrence(arr, 0, arr.length - 1, value);
	}
	
	private int binarySearchRecursiveLastOccurrence(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value && (middle == arr.length - 1 || value < arr[middle + 1])) {
				return middle;
			} else if (arr[middle] > value) {
				return binarySearchRecursiveLastOccurrence(arr, low, middle - 1, value);
			} else {
				return binarySearchRecursiveLastOccurrence(arr, middle + 1, high, value);
			}
		}
		return -1;
	}

}
