package av_binary;

public class NextGreatestAlphabet {
	
	public static int nextGreatestAlphabet(char[] arr, char value) {
		if (value < arr[0] || value >= arr[arr.length - 1]) {
			return 0;
		}
		return searchRecursive(arr, 0, arr.length - 1, value);
	}
	
	private static int searchRecursive(char[] arr, int low, int high, char value) {
		if (low <= high) {
			int middle = low + (high - low) / 2;
			if (arr[middle] == value && middle < arr.length - 1) {
				return middle + 1;
			} else if (arr[middle] < value && middle < arr.length - 1 && value < arr[middle + 1]) {
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
