// JAVA Code for Dynamic Programming | 
// Set 9 (Binomial Coefficient) 
import java.util.*; 

public class BinomialCoefficient { 

	// Returns value of Binomial Coefficient C(n, k) 
	static int binomialCoeffRecursive(int n, int k) {

		// Base Cases 
		if (k == 0 || k == n) 
			return 1; 

		// Recur 
		return binomialCoeffRecursive(n - 1, k - 1) + 
				binomialCoeffRecursive(n - 1, k); 
	} 

	// Returns value of Binomial Coefficient C(n, k) 
	static int binomialCoeff(int n, int k) {
		int C[][] = new int[n+1][k+1]; 
        printMatrix(C, n + 1, k + 1, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		int i, j; 

		// Calculate value of Binomial Coefficient in bottom up manner 
		for (i = 0; i <= n; i++) {
			for (j = 0; j <= Math.min(i, k); j++) {
				System.out.println("Math.min(i, k) : " + Math.min(i, k));
				System.out.println("i : " + i);
				System.out.println("j : " + j);
				if (j == 0 || j == i) {  // Base Cases
					C[i][j] = 1; 
				} else {  // Calculate value using previously stored values
					C[i][j] = C[i-1][j-1] + C[i-1][j]; 
				}
                printMatrix(C, n + 1, k + 1, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			} 
		} 

		return C[n][k];
	} 

	static int binomialCoeffOnSpace(int n, int k) {
		int C[] = new int[k + 1];
		printArray(C, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		// nC0 is 1 
		C[0] = 1; 
	
		for (int i = 1; i <= n; i++) {
			// Compute next row of pascal triangle using the previous row
			// This table is filled in reverse order in rows with j--, otherwise it will give the wrong
			// result. It is because, if the row will be filled in forward order, then some of the generated
			// values will be required in the next step of j and it will already be changed at that time.
			for (int j = Math.min(i, k); j > 0; j--) {
				System.out.println("i : " + i);
				System.out.println("j : " + j);
				C[j] = C[j] + C[j-1];
				printArray(C, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
		} 
		return C[k]; 
	} 
	
	/* Driver program to test above function */
	public static void main(String[] args) {
		int n = 5, k = 3; 
		System.out.printf("Value of C(%d, %d) is %d ", n, k, binomialCoeffRecursive(n, k)); 
		System.out.println();
		System.out.printf("Value of C(%d, %d) is %d ", n, k, binomialCoeff(n, k)); 
		System.out.println();
		System.out.printf("Value of C(%d, %d) is %d ", n, k, binomialCoeffOnSpace(n, k)); 
		System.out.println();
	}
	
	static void printMatrix(int[][] matrix, int m, int n, String delimiter) {
		System.out.println(delimiter);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (matrix[i][j] == 2147483647) {
					System.out.print(" M" + " ");
				} else if (matrix[i][j] >= 0 && matrix[i][j] < 10) {
					System.out.print(" " + matrix[i][j] + " ");
				} else {
					System.out.print(matrix[i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
	
	static void printArray(int[] arr, String delimiter) {
		System.out.println(delimiter);
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
	
}
