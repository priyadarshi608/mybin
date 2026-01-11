// Dynamic Programming implementation of Matrix Chain Multiplication. 

public class MCM {

	static int matrixChainOrderRecursive(int p[], int i, int j) {
        if (i == j) 
            return 0; 
  
        int min = Integer.MAX_VALUE; 
  
        // place parenthesis at different places between first 
        // and last matrix, recursively calculate count of 
        // multiplications for each parenthesis placement and 
        // return the minimum count 
        for (int k=i; k<j; k++) 
        { 
            int count = matrixChainOrderRecursive(p, i, k) + 
            			matrixChainOrderRecursive(p, k+1, j) + 
                        p[i-1]*p[k]*p[j]; 
  
            if (count < min) 
                min = count; 
        } 
  
        // Return minimum count 
        return min;
    }
	
//	static int matrixChainOrder(int p[], int n) {
//		int m[][] = new int[n][n];
//
//		int i, j, k, L, q; 
//
//		for (i = 1; i < n; i++) {
//			m[i][i] = 0; 
//		}
//
//		for (L = 2; L < n; L++) {
//			for (i = 1; i < n - L + 1; i++) {
//				j = i + L - 1;
//				if(j == n)
//					continue; 
//				m[i][j] = Integer.MAX_VALUE;
//				for (k = i; k < j; k++) {
//					q = m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j]; 
//					if (q < m[i][j]) {
//						m[i][j] = q; 
//					}
//				} 
//			} 
//		} 
//		return m[1][n-1]; 
//	} 

	// Driver program to test above function 
	public static void main(String args[]) {
		int arr[] = new int[] {1, 2, 3, 4, 5};
//		int arr[] = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
		int size = arr.length;

		System.out.println("Minimum number of multiplications is "+ 
						matrixChainOrder(arr, size)); 

		System.out.println("Minimum number of multiplications is "+ 
				matrixChainOrderRecursive(arr, 1, size - 1)); 
	}
	
	// Matrix Ai has dimension p[i-1] x p[i] for i = 1..n 
	static int matrixChainOrder(int p[], int n) {
		/* For simplicity of the program, one extra row and one 
		extra column are allocated in m[][]. 0th row and 0th 
		column of m[][] are not used */
		int m[][] = new int[n][n];
		initializeMatrix(m, -1); // just to print and see the result

		int i, j, k, L, q; 

		/* m[i,j] = Minimum number of scalar multiplications needed 
		to compute the matrix A[i]A[i+1]...A[j] = A[i..j] where 
		dimension of A[i] is p[i-1] x p[i] */

		// cost is zero when multiplying one matrix. 
		for (i = 1; i < n; i++) {
			m[i][i] = 0; 
		}
		printMatrix(m, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		// L is chain length.
		System.out.println("n = " + n);
		for (L = 2; L < n; L++) {
			System.out.println("L = " + L);
			for (i = 1; i < n - L + 1; i++) {
				System.out.println("i = " + i);
				j = i + L - 1;
				System.out.println("j = " + j);
				if(j == n)
					continue; 
				m[i][j] = Integer.MAX_VALUE;
				System.out.println("i : " + i);
				System.out.println("j : " + j);
				printMatrix(m, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				for (k = i; k < j; k++) {
					printMatrix(m, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					// q = cost/scalar multiplications
					System.out.println("i : " + i);
					System.out.println("j : " + j);
					System.out.println("k : " + k);
					q = m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j]; 
					if (q < m[i][j]) {
						m[i][j] = q; 
					}
					printMatrix(m, "##########################################################");
				} 
			} 
		} 

		return m[1][n-1]; 
	}
	
	static void printMatrix(int[][] matrix, String delimiter) {
		System.out.println(delimiter);
		int m = matrix.length;
		int n = matrix.length;
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
	
	static void initializeMatrix(int[][] matrix, int initVal) {
		int m = matrix.length;
		int n = matrix.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = initVal;
			}
		}
	}
	
} 
