// Java program for implementation of Heap Sort
class HeapSort {
	
    public int[] sort(int suppliedArr[]) {
		// check for empty or null array
		if (suppliedArr == null || suppliedArr.length == 0){
			return null;
		}
		int[] arr = suppliedArr;
    	
        int n = arr.length;
 
        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, i, n);
 
        // One by one extract an element from heap
        for (int i=n-1; i>=0; i--) {
            // Move current root to end
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
 
            // call max heapify on the reduced heap
            heapify(arr, 0, i);
        }
        
        return arr;
    }
 
    // To heapify a subtree rooted with node i which is
    // an index in arr[]. n is size of heap
    void heapify(int[] numbers, int root, int heapSize) {
		int largest = root;
		int l = 2*root + 1;
		int r = 2*root + 2;
 
        // If left child is larger than root
		if (l < heapSize && numbers[l] > numbers[largest]) {
			largest = l;
		}
 
        // If right child is larger than largest so far
		if (r < heapSize && numbers[r] > numbers[largest]) {
			largest = r;
		}
 
        // If largest is not root
		if (largest != root) {
			int temp = numbers[largest];
			numbers[largest] = numbers[root];
			numbers[root] = temp;
			
            // Recursively heapify the affected sub-tree
			heapify(numbers, largest, heapSize);
		}
    }
}
