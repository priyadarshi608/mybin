package av_binary;

public class MaxElementInBitonicArray {
	
	public static int maxElementInBitonicArray(int[] arr) {
		return findMaximum(arr, 0, arr.length - 1);
	}
	
	private static int findMaximum(int[] arr, int low, int high) {
		/* Base Case: Only one element is present in arr[low..high]*/
		if (low == high)
			return low;
		
		/* If there are two elements and first is greater then the first
			element is maximum */
		if ((high == low + 1) && arr[low] >= arr[high])
			return low;
		
		/* If there are two elements and second is greater then the second
			element is maximum */
		if ((high == low + 1) && arr[low] < arr[high])
			return high;
			
		int mid = low + (high - low)/2;
		
		/* If we reach a point where arr[mid] is greater than both of its adjacent
			elements arr[mid-1] and arr[mid+1], then arr[mid] is the maximum element*/
		if ( arr[mid] > arr[mid + 1] && arr[mid] > arr[mid - 1])
			return mid;
		
		/* If arr[mid] is greater than the next element and smaller than the previous
			element then maximum lies on left side of mid */
		if (arr[mid] > arr[mid + 1] && arr[mid] < arr[mid - 1])
			return findMaximum(arr, low, mid-1);
		else
			return findMaximum(arr, mid + 1, high);
	}

}
