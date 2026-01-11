/**
 * This is the implementation class of the quick sort. Its sort method can be 
 * used to sort the elements of the array in an average time of O(nlogn).
 * 
 * @author shubham
 */
public class QuickSort 
{ 
	void sort(int arr[]) {
		if (arr != null)
			sortRecursively(arr, 0, arr.length - 1);
	}
	
	/* The main function that implements QuickSort() 
	arr[] --> Array to be sorted, 
	low --> Starting index, 
	high --> Ending index */
	void sortRecursively(int arr[], int low, int high) 
	{ 
		if (low < high) 
		{ 
			/* arr[partitionIndex] is now at right place */
			int partitionIndex = partition(arr, low, high);

			// Recursively sort elements before 
			// partition and after partition 
			sortRecursively(arr, low, partitionIndex - 1);
			sortRecursively(arr, partitionIndex + 1, high);
		} 
	} 

	/* This function takes last element as pivot, 
	places the pivot element at its correct 
	position in sorted array, and places all 
	smaller (smaller than pivot) to left of 
	pivot and all greater elements to right 
	of pivot */
	private int partition(int[] arr, int low, int high) {
		int pivot = arr[high];
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (arr[j] < pivot) {
				i++;
				swap(arr, i, j);
			}
		}
		swap(arr, i + 1, high);
		return i + 1;
	}

	private void swap (int[] arr, int firstIndex, int secondIndex) {
		int temp = arr[secondIndex];
		arr[secondIndex] = arr[firstIndex];
		arr[firstIndex] = temp;
	}
} 
