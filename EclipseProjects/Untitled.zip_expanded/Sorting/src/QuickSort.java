import java.util.Stack;

public class QuickSort implements Sort {
	
	public int[] sort(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return arr;
		}
//		quickSort(arr, 0, arr.length - 1);
//		quickSortTailRecursive(arr, 0, arr.length - 1);
//		quickSortIterative1(arr, 0, arr.length - 1);
		quickSortIterative2(arr, 0, arr.length - 1);  // V. V. I.
		return arr;
	}
	
	private void quickSort(int[] arr, int low, int high) {
		if (low < high) {
			int partitionedIndex = partition(arr, low, high);
			quickSort(arr, low, partitionedIndex - 1);
			quickSort(arr, partitionedIndex + 1, high);
		}
	}
	
	private int partition(int[] arr, int low, int high) {
		int pivot = arr[high];
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (arr[j] < pivot) {
				i++;
				SortingUtility.swap(arr, i, j);
			}
		}
		SortingUtility.swap(arr, i + 1, high);
		return i + 1;
	}
	
	private void quickSortTailRecursive(int[] arr, int low, int high) {
		while (low < high) {
			int partitionedIndex = partition(arr, low, high);
			quickSort(arr, low, partitionedIndex - 1);
			low = partitionedIndex + 1;
		}
	}
	
	private void quickSortIterative1(int[] arr, int low, int high) {
		Stack<Integer> stack = new Stack<>();
		stack.push(low);
		stack.push(high);
		while(!stack.isEmpty()) {
			high = stack.pop();
			low = stack.pop();
			int partition = partition(arr, low, high);
			if (partition - 1 > low) {
				stack.push(low);
				stack.push(partition - 1);
			}
			if (partition + 1 < high) {
				stack.push(partition + 1);
				stack.push(high);
			}
		}
	}
	
	private void quickSortIterative2(int arr[], int l, int h) {
        // create auxiliary stack 
        int stack[] = new int[h - l + 1]; 
  
        // initialize top of stack 
        int top = -1; 
  
        // push initial values in the stack 
        stack[++top] = l; 
        stack[++top] = h; 
  
        // keep popping elements until stack is not empty 
        while (top >= 0) { 
            // pop h and l 
            h = stack[top--]; 
            l = stack[top--]; 
  
            // set pivot element at it's proper position 
            int p = partition(arr, l, h); 
  
            // If there are elements on left side of pivot, 
            // then push left side to stack 
            if (p - 1 > l) { 
                stack[++top] = l; 
                stack[++top] = p - 1; 
            } 
  
            // If there are elements on right side of pivot, 
            // then push right side to stack 
            if (p + 1 < h) { 
                stack[++top] = p + 1; 
                stack[++top] = h; 
            } 
        }
	}
	
}
