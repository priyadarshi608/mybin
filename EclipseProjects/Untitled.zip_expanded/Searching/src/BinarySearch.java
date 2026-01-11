
public class BinarySearch implements Search {

	@Override
	public int search(int[] arr, int value) {
//		return binarySearch(arr, 0, arr.length - 1, value);
		return binarySearchRecursive(arr, 0, arr.length - 1, value);
	}
	
	private int binarySearchRecursive(int[] arr, int low, int high, int value) {
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

	private int binarySearch(int arr[], int l, int r, int x) {
		while (l <= r) { 
			int m = l + (r - l) / 2; 

			// Check if x is present at mid 
			if (arr[m] == x) 
				return m; 

			// If x greater, ignore left half 
			if (arr[m] < x) 
				l = m + 1; 

			// If x is smaller, ignore right half 
			else
				r = m - 1; 
		} 

		// if we reach here, then element was 
		// not present 
		return -1; 
	} 

}
