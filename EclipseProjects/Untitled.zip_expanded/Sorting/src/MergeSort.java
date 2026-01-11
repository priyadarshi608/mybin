
public class MergeSort implements Sort {
	
	int[] helper;
	
	public int[] sort(int[] arr) {
		if (arr == null || arr.length == 0) {
			return arr;
		}
		helper = new int[arr.length];
		mergeSort(arr, 0, arr.length - 1);
		return arr;
	}
	
	private void mergeSort(int[] arr, int low, int high) {
		if (low < high) {
			int middle = low + (high - low) / 2;
			mergeSort(arr, low, middle);
			mergeSort(arr, middle + 1, high);
			merge(arr, low, middle, high);
		}
	}
	
	private void merge(int[] arr, int low, int middle, int high) {
		for (int i = low; i <= high; i++) {
			helper[i] = arr[i];
		}
		int i = low;
		int j = middle + 1;
		int k = low;
		
		// Copy the smallest values from either the left or the right side back
		// to the original array
		while (i <= middle && j <= high) {
			if (helper[i] <= helper[j]) {
				arr[k] = helper[i];
				i++;
			} else {
				arr[k] = helper[j];
				j++;
			}
			k++;
		}
		
		// Copy the rest of the left side of the array into the target array
		while (i <= middle) {
			arr[k] = helper[i];
			i++;
			k++;
		}
		
		// Copy the rest of the right side of the array into the target array
		while (j <= high) {
			arr[k] = helper[j];
			j++;
			k++;
		}
	}
	
}
