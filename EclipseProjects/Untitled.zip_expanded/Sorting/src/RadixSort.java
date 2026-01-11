import java.util.Arrays;

public class RadixSort implements Sort {

	public int[] sort(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return arr;
		}
		radixSort(arr);
		return arr;
	}
	
	private void radixSort(int[] arr) {
        // Find the maximum number to know number of digits 
        int max = getMax(arr); 
  
        // Do counting sort for every digit. Note that instead 
        // of passing digit number, exp is passed. exp is 10^i 
        // where i is current digit number 
        for (int exp = 1; max/exp > 0; exp *= 10) {
            countSort(arr, exp);
            printArray(arr);
        }
    }
	
	private void countSort(int arr[], int exp) {
        int output[] = new int[arr.length]; // output array 
        int i; 
        int count[] = new int[10]; 
        Arrays.fill(count,0); 
  
        // Store count of occurrences in count[] 
        for (i = 0; i < arr.length; i++) 
            count[ (arr[i]/exp)%10 ]++; 
  
        // Change count[i] so that count[i] now contains 
        // actual position of this digit in output[] 
        for (i = 1; i < 10; i++) 
            count[i] += count[i - 1]; 
  
        // Build the output array but scan arr in reverse order to make the output stable
        for (i = arr.length - 1; i >= 0; i--) { 
            output[count[ (arr[i]/exp)%10 ] - 1] = arr[i]; 
            count[ (arr[i]/exp)%10 ]--; 
        } 
  
        // Copy the output array to arr[], so that arr[] now 
        // contains sorted numbers according to curent digit 
        for (i = 0; i < arr.length; i++) 
            arr[i] = output[i];
    }
	
	private int getMax(int arr[]) { 
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) 
            if (arr[i] > max) 
                max = arr[i]; 
        return max;
    }
	
	private void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + ", ");
		}
		System.out.println();
	}
	
}
