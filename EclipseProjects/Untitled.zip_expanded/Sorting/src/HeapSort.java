
public class HeapSort implements Sort {
	
	public int[] sort(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return arr;
		}
		heapSort(arr);
		return arr;
	}

	private void heapSort(int[] arr) {
		int middle = arr.length / 2;
		for (int i = middle - 1; i >= 0; i--) {
			heapify(arr, i, arr.length);
		}
		for (int i = arr.length - 1; i >= 0; i--) {
			SortingUtility.swap(arr, 0, i);
			heapify(arr, 0, i);
		}
	}
	
	private void heapify(int[] arr, int root, int heapSize) {
		int largest = root;
		int l = 2*root + 1;
		int r = 2*root + 2;
		
		if (l < heapSize && arr[l] > arr[largest]) {
			largest = l;
		}
		
		if (r < heapSize && arr[r] > arr[largest]) {
			largest = r;
		}
		
		if (largest != root) {
			SortingUtility.swap(arr, root, largest);
			heapify(arr, largest, heapSize);
		}
	}
}
