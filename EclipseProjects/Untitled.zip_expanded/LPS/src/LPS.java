//Java program of above approach 

class LPS {

	// Returns the length of the longest palindromic subsequence in seq 
	static int _lps(char seq[], int i, int j) { 
		// Base Case 1: If there is only 1 character 
		if (i == j) { 
			return 1; 
		} 

		// Base Case 2: If there are only 2 characters and both are same 
		if (seq[i] == seq[j] && i + 1 == j) { 
			return 2; 
		} 

		// If the first and last characters match 
		if (seq[i] == seq[j]) { 
			return _lps(seq, i + 1, j - 1) + 2; 
		} 

		// If the first and last characters do not match 
		return max(_lps(seq, i, j - 1), _lps(seq, i + 1, j)); 
	} 

	static int lps(String seq) {
		int n = seq.length(); 
		int i, j, cl; 
		// Create a table to store results of subproblems 
		int L[][] = new int[n][n];
		initializeMatrix(L, 0);

		// Strings of length 1 are palindrome of lentgh 1 
		for (i = 0; i < n; i++) 
			L[i][i] = 1; 

		printMatrix(L, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		// cl is length of substring 
		for (cl=2; cl<=n; cl++) {
			for (i=0; i<n-cl+1; i++) {
				j = i+cl-1;
				System.out.println("cl : " + cl);
				System.out.println("i : " + i);
				System.out.println("j : " + j);
				if (seq.charAt(i) == seq.charAt(j) && cl == 2) 
					L[i][j] = 2; 
				else if (seq.charAt(i) == seq.charAt(j)) 
					L[i][j] = L[i+1][j-1] + 2; 
				else
					L[i][j] = max(L[i][j-1], L[i+1][j]); 
				printMatrix(L, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			} 
		} 

		return L[0][n-1]; 
	} 

	/* Driver program to test above function */
	public static void main(String[] args) { 
		String seq = "GEEKSFORGEEKS"; 
		int n = seq.length(); 
		System.out.printf("The length of the LPS is %d", lpsRecursive(seq));
		System.out.println();
		System.out.printf("The length of the LPS is %d", lps(seq)); 
	}
	
	static int lpsRecursive(String seq) {
		int n = seq.length(); 
		return _lps(seq.toCharArray(), 0, n - 1);
	}

	// A utility function to get max of two integers 
	static int max(int x, int y) { 
		return (x > y) ? x : y; 
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
