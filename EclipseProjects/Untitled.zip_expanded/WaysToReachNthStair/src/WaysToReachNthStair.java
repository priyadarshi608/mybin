// Java program to count number of ways to reach n't stair when 
// a person can climb 1, 2, ..m stairs at a time 

public class WaysToReachNthStair {

	// Time Complexity of this solution is exponential.
	static int countWaysRecursive(int s, int m) {
		return countWaysRecursiveUtil(s+1, m); 
	} 

	static int countWaysRecursiveUtil(int n, int m) {
		if (n <= 1) 
			return n; 
		int res = 0; 
		for (int i = 1; i<=m && i<=n; i++) 
			res += countWaysRecursiveUtil(n-i, m); 
		return res; 
	} 

	// Time Complexity = O(sm)
	static int countWaysDP(int s, int m) {
		return countWaysDPUtil(s+1, m); 
	} 

	static int countWaysDPUtil(int n, int m) {
		int res[] = new int[n]; 
		res[0] = 1; res[1] = 1; 
		for (int i=2; i<n; i++) {
			res[i] = 0; 
			for (int j=1; j<=m && j<=i; j++) 
				res[i] += res[i-j]; 
		} 
		return res[n-1]; 
	} 
	
	// Driver method 
	public static void main(String[] args) {
		int s = 4, m = 3; 
		System.out.println("Nuber of ways = " + countWaysRecursive(s, m));
		System.out.println("Nuber of ways = " + countWaysDP(s, m));
	} 
} 
