// A naive recursive implementation of optimal binary search tree problem
public class OptimalBST {

	static int optCostDynamicProgramming(int[] freq) {
        int T[][] = new int[freq.length][freq.length];
        
        for(int i=0; i < T.length; i++){
            T[i][i] = freq[i];
        }
        
        for(int l = 2; l <= freq.length; l++) {
            for(int i = 0; i <= freq.length-l + 1; i++) {
                int j = i + l -1;
                T[i][j] = Integer.MAX_VALUE;
                int sum = sum(freq, i, j);
                
                for(int k = i; k <= j; k++){
                     int val = sum + (k-1 < i ? 0 : T[i][k-1]) +
                            (k+1 > j ? 0 : T[k+1][j]);
                     if(val < T[i][j]){
                         T[i][j] = val;
                     }
                }
            }
        }
        return T[0][freq.length-1];
	}

	// A recursive function to calculate cost of optimal binary search tree 
	static int optCost(int freq[], int i, int j) {
		// Base cases 
		if (j < i)	 // no elements in this subarray 
			return 0; 
		if (j == i)	 // one element in this subarray 
			return freq[i]; 

		// Get sum of freq[i], freq[i+1], ... freq[j] 
		int fsum = sum(freq, i, j); 

		// Initialize minimum value 
		int min = Integer.MAX_VALUE; 

		// One by one consider all elements as root and 
		// recursively find cost of the BST, compare the 
		// cost with min and update min if needed 
		for (int r = i; r <= j; ++r) {
			int cost = optCost(freq, i, r-1) + optCost(freq, r+1, j); 
			if (cost < min) 
				min = cost; 
		} 

		// Return minimum value 
		return min + fsum; 
	} 

	// Driver code 
	public static void main(String[] args) { 
		int keys[] = {10, 12, 20}; 
		int freq[] = {34, 8, 50}; 
		int n = keys.length; 
		System.out.println("Cost of Optimal BST is " + 
				optimalSearchTree(keys, freq, n)); 
	}
	
	// The main function that calculates minimum cost of 
	// a Binary Search Tree. It mainly uses optCost() to 
	// find the optimal cost. 
	static int optimalSearchTree(int keys[], int freq[], int n) {
		// *********************************************************
		// *********************   V V I  **************************
		// *********************************************************
		// ADD THE CODE TO SORT KEYS IN INCREASING ORDER IF REQUIRED
		// *********************************************************
		// *********************************************************
//		return optCost(freq, 0, n-1);
		return optCostDynamicProgramming(freq);
	}

	// A utility function to get sum of array elements 
	// freq[i] to freq[j] 
	static int sum(int freq[], int i, int j) {
		int s = 0; 
		for (int k = i; k <=j; k++) 
			s += freq[k]; 
		return s; 
	} 

} 
