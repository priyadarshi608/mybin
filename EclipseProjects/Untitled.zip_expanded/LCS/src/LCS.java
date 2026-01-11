/* A Naive recursive implementation of LCS problem in java*/
public class LCS {

	/* Returns length of LCS for X[0..m-1], Y[0..n-1] */
	int lcsRecursive( char[] X, char[] Y, int m, int n ) {
		if (m == 0 || n == 0) 
			return 0; 
		if (X[m-1] == Y[n-1]) 
			return 1 + lcsRecursive(X, Y, m-1, n-1); 
		else
			return max(lcsRecursive(X, Y, m, n-1), lcsRecursive(X, Y, m-1, n)); 
	} 

	int lcs( char[] X, char[] Y, int m, int n ) {
		int L[][] = new int[m+1][n+1];
		initializeMatrix(L, -1);

		/* Following steps build L[m+1][n+1] in bottom up fashion. Note 
			that L[i][j] contains length of LCS of X[0..i-1] and Y[0..j-1] */
		for (int i=0; i<=m; i++) {
			for (int j=0; j<=n; j++) {
//				System.out.println("i : " + i);
//				System.out.println("j : " + j);
//				printMatrix(L, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				if (i == 0 || j == 0)  // This is because the size of the matrix is m+1 and n+1.
					L[i][j] = 0; 
				else if (X[i-1] == Y[j-1]) 
					L[i][j] = L[i-1][j-1] + 1; 
				else
					L[i][j] = max(L[i-1][j], L[i][j-1]); 
//				printMatrix(L, "################################################################");
			} 
		} 
		return L[m][n]; 
	} 

	public static void main(String[] args) {
		LCS lcs = new LCS();
//		String s1 = "A";
//		String s2 = "A";
		String s1 = "AGGTAB"; 
		String s2 = "GXTXAYB"; 

		char[] X=s1.toCharArray(); 
		char[] Y=s2.toCharArray(); 
		int m = X.length;
		int n = Y.length;

//		System.out.println("Length of LCS is" + " " + lcs.lcsRecursive( X, Y, m, n )); 
		System.out.println("Length of LCS is" + " " + lcs.lcs( X, Y, m, n )); 
	} 

	/* Utility function to get max of 2 integers */
	int max(int a, int b) {
		return (a > b)? a : b; 
	} 

	void printMatrix(int[][] matrix, String delimiter) {
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
	
	void initializeMatrix(int[][] matrix, int initVal) {
		int m = matrix.length;
		int n = matrix.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = initVal;
			}
		}
	}
	
} 
