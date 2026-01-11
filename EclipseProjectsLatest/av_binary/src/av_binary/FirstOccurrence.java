package av_binary;

public class FirstOccurrence {

	public static int search(int[] arr, int value) {
		FirstOccurrence firstOccurrence = new FirstOccurrence();
		return firstOccurrence.binarySearchRecursiveFirstOccurrence(arr, 0, arr.length - 1, value);
	}
	
	private int binarySearchRecursiveFirstOccurrence(int[] arr, int low, int high, int value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value && (middle == 0 || arr[middle - 1] < value)) {
				return middle;
			} else if (arr[middle] > value) {
				return binarySearchRecursiveFirstOccurrence(arr, low, middle - 1, value);
			} else {
				return binarySearchRecursiveFirstOccurrence(arr, middle + 1, high, value);
			}
		}
		return -1;
	}

}
