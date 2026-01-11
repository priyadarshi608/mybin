public class QuickSortDemo {
	
	public static void main(String[] argss) {
		int[] numbers = {43, 11, 23, 25, 57, 16, 33, 19, 58};
		QuickSort quickSort = new QuickSort();
		quickSort.sort(numbers);
	}
	
	/* A utility function to print array of size n */
	static void printArray(int arr[]) {
		int n = arr.length; 
		for (int i=0; i<n; ++i) 
			System.out.print(arr[i]+" "); 
		System.out.println(); 
	} 

}
