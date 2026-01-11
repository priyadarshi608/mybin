package av_binary;

public class RotationInSortedArray {
	
	public static int rotationInSortedArray(int[] arr) {
		if (arr.length <= 1) {
			return 0;
		}
		if (arr[0] < arr[arr.length - 1]) {
			return 0;
		}
		return indexOfMinElement(arr, 0, arr.length - 1);
//		return indexOfMinElementIterative(arr);
	}
	
	private static int indexOfMinElement(int[] arr, int low, int high) {
		// Here we don't need to return -1 because array will always have a min element.
		if (arr[low] <= arr[high]) {
			return low;
		}
		int middle = low + (high - low) / 2;
		int next = (middle + 1) % arr.length;
		int prev = (middle - 1 + arr.length) % arr.length;
		if (arr[middle] <= arr[prev] && arr[middle] <= arr[next]) {
			return middle;
		} else if (arr[middle] > arr[high]) {
			return indexOfMinElement(arr, middle + 1, high);
		} else {
			return indexOfMinElement(arr, low, middle - 1);
		}
	}

	private static int indexOfMinElementIterative(int[] arr) {
		int pivot = -1;
		int low = 0, high = arr.length - 1;

		while(low <= high){
			if(arr[low] <= arr[high]){
				return low;
			}

			int mid = low + (high-low)/2;
			int next = (mid+1) % arr.length;
			int prev = (mid-1+arr.length) % arr.length;

			if(arr[mid] <= arr[next] && arr[mid] <= arr[prev]){
				pivot = mid;
				break;
			}

			if(arr[high] < arr[mid]){
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		return pivot;
	}
	
}
