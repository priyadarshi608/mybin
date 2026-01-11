/* A Naive recursive implementation of 0-1 Knapsack problem */
class Knapsack 
{ 

	// A utility function that returns maximum of two integers 
	static int max(int a, int b) { return (a > b)? a : b; } 

	// Returns the maximum value that can be put in a knapsack of capacity W 
	static int knapSackRecursive(int W, int wt[], int val[], int n) 
	{ 
		// Base Case 
		if (n == 0 || W == 0) 
			return 0; 

		// If weight of the nth item is more than Knapsack capacity W, then 
		// this item cannot be included in the optimal solution 
		if (wt[n-1] > W) 
			return knapSackRecursive(W, wt, val, n-1); 

		// Return the maximum of two cases: 
		// (1) nth item included 
		// (2) not included 
		else return max( val[n-1] + knapSackRecursive(W-wt[n-1], wt, val, n-1), 
				knapSackRecursive(W, wt, val, n-1) 
				); 
	} 

	static int knapSack(int W, int wt[], int val[], int n) {
		int i, w; 
		int K[][] = new int[n+1][W+1];
		printMatrix(K, n + 1, W + 1, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		// Build table K[][] in bottom up manner 
		for (i = 0; i <= n; i++) {
			for (w = 0; w <= W; w++) {
				System.out.println("i : " + i);
				System.out.println("w : " + w);
				if (i==0 || w==0) 
					K[i][w] = 0; 
				else if (wt[i-1] <= w) 
					K[i][w] = max(val[i-1] + K[i-1][w-wt[i-1]], K[i-1][w]); 
				else
					K[i][w] = K[i-1][w]; 
				printMatrix(K, n + 1, W + 1, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			} 
		} 

		return K[n][W]; 
	} 

	// Driver program to test above function 
	public static void main(String args[]) {
		int val[] = new int[]{6, 10, 12}; 
		int wt[] = new int[]{1, 2, 3}; 
		int W = 5;
		int n = val.length;
//		System.out.println(knapSackRecursive(W, wt, val, n)); 
		System.out.println(knapSack(W, wt, val, n)); 
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
	
} 
