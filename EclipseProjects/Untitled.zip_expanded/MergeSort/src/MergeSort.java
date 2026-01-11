
public class MergeSort {

	public int[] sort(int[] values) {
		// check for empty or null array
		if (values == null){
			return null;
		}
		// check for empty or null array
		if (values.length == 0){
			return values;
		}
		mergesort(values, 0, values.length - 1);
		return values;
	}

	private void mergesort(int[] numbers, int low, int high) {
		// check if low is smaller then high, if not then the array is sorted
		if (low < high) {
			// Get the index of the element which is in the middle
			int middle = low + (high - low) / 2;
			// Sort the left side of the array
			mergesort(numbers, low, middle);
			// Sort the right side of the array
			mergesort(numbers, middle + 1, high);
			// Combine them both
			merge(numbers, low, middle, high);
		}
	}

	private void merge(int[] numbers, int low, int middle, int high) {
		int[] helper = new int[numbers.length];

		// Copy both parts into the helper array
		for (int i = low; i <= high; i++) {
			helper[i] = numbers[i];
		}

		int i = low;
		int j = middle + 1;
		int k = low;
		// Copy the smallest values from either the left or the right side back
		// to the original array
		while (i <= middle && j <= high) {
			if (helper[i] <= helper[j]) {
				numbers[k] = helper[i];
				i++;
			} else {
				numbers[k] = helper[j];
				j++;
			}
			k++;
		}

		// Copy the rest of the left side of the array into the target array
		while (i <= middle) {
			numbers[k] = helper[i];
			k++;
			i++;
		}

		// Copy the rest of the right side of the array into the target array
		while (j <= high) {
			numbers[k] = helper[j];
			k++;
			j++;
		}

	}
}
