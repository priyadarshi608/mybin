import junit.extensions.TestSetup;

/**
 * This is the implementation class of the quick sort. Its sort method can be 
 * used to sort the elements of the array in an average time of O(nlogn).
 * 
 * @author shubham
 */
public class QuickSort  {

	private int[] numbers;
	private int number;

	public int[] sort(int[] values) {
		// check for empty or null array
		if (values ==null){
			return null;
		}
		else if (values.length == 0) {
			return values;
		}
		
		this.numbers = values;
		number = values.length;
		
		quicksort(0, number - 1);
		printNumbers();
		return numbers;
	}

	private void quicksort(int low, int high) {
		int left = low, right = high;
		boolean leftward = true;
		int pivotpos = low;
		int pivot = numbers[low];
		while (left < right) {
			if (leftward) {
				while (pivot < numbers[right]) {
					right--;
				}
				pivotpos = right;
				exchange(left, right);
				left++;
				leftward = false;
			} else {
				while (pivot > numbers[left]) {
					left++;
				}
				pivotpos = left;
				exchange(left, right);
				right--;
				leftward = true;
			}
		}
				
		if (low < pivotpos - 1)
			quicksort(low, pivotpos - 1);
		if (high > pivotpos + 1)
			quicksort(pivotpos + 1, high);
	}
	
	private void exchange(int i, int j) {
		int temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;
	}

	void printNumbers() {
		for (int i = 0; i < numbers.length; i++) {
			System.out.print(numbers[i] + ", ");
		}
		System.out.println();
	}
}
