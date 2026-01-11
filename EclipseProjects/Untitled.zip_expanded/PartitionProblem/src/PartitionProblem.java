// A dynamic programming based Java program for partition problem 
import java.io.*; 

public class PartitionProblem { 

	static boolean isSubsetSum (int arr[], int n, int sum) {
		// Base Cases 
		if (sum == 0) 
			return true; 
		if (n == 0 && sum != 0) 
			return false; 

		// If last element is greater than sum, then ignore it 
		if (arr[n-1] > sum) 
			return isSubsetSum (arr, n-1, sum); 

		/* else, check if sum can be obtained by any of 
		the following 
		(a) including the last element 
		(b) excluding the last element 
		*/
		return isSubsetSum (arr, n-1, sum) || 
			isSubsetSum (arr, n-1, sum-arr[n-1]); 
	} 

	// Returns true if arr[] can be partitioned in two 
	// subsets of equal sum, otherwise false 
	static boolean findPartitionRecursive(int arr[], int n) {
		// Calculate sum of the elements in array 
		int sum = 0; 
		for (int i = 0; i < n; i++) 
			sum += arr[i]; 

		// If sum is odd, there cannot be two subsets 
		// with equal sum 
		if (sum%2 != 0) 
			return false; 

		// Find if there is subset with sum equal to half 
		// of total sum 
		return isSubsetSum (arr, n, sum/2); 
	} 

	// Returns true if arr[] can be partitioned in two subsets of 
	// equal sum, otherwise false 
	static boolean findPartition (int arr[], int n) {
		int sum = 0; 
		int i, j; 

		// Caculcate sun of all elements 
		for (i = 0; i < n; i++) 
			sum += arr[i]; 

		if (sum%2 != 0) 
			return false; 

		boolean part[][]=new boolean[sum/2+1][n+1]; 

		// initialize top row as true 
		for (i = 0; i <= n; i++) 
			part[0][i] = true; 

		// initialize leftmost column, except part[0][0], as 0 
		for (i = 1; i <= sum/2; i++) 
			part[i][0] = false; 

		printMatrix(part, sum/2 + 1, n + 1, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// Fill the partition table in botton up manner 
		for (i = 1; i <= sum/2; i++) {
			for (j = 1; j <= n; j++) {
				System.out.println("i : " + i);
				System.out.println("j : " + j);
				System.out.println("arr[j-1] : " + arr[j-1]);
				part[i][j] = part[i][j-1]; 
				printMatrix(part, sum/2 + 1, n + 1, "---------------------------------------------------------------");
				if (i >= arr[j-1]) {
					part[i][j] = part[i][j] || part[i - arr[j-1]][j-1];
					System.out.println("part[i - arr[j-1]][j-1] : part[" + (i - arr[j-1]) + "][" + (j-1) + "]");
				}
				printMatrix(part, sum/2 + 1, n + 1, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			} 
		} 

		return part[sum/2][n]; 
	} 

	/*Driver function to check for above function*/
	public static void main (String[] args) {
		int arr[] = {3, 1, 1, 2, 2,1}; 
		int n = arr.length; 
		if (findPartition(arr, n) == true)
			System.out.println("Can be divided into two subsets of equal sum");
		else
			System.out.println("Can not be divided into two subsets of equal sum");
	}
	
	static void printMatrix(boolean[][] matrix, int m, int n, String delimiter) {
		System.out.println(delimiter);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (matrix[i][j] == true) {
					System.out.print("+true" + " ");
				} else {
					System.out.print(matrix[i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
	
} 
