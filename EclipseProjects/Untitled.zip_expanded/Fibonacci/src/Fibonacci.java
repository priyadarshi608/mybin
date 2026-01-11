/**
 * https://www.geeksforgeeks.org/program-for-nth-fibonacci-number/
 * @author shubham
 */
public class Fibonacci {

	static final int MAX = 1000; 
	static final int NIL = -1;
	static final int lookupTableForDPMemoization[] = new int[MAX]; 
	static final int lookupTableForFormulaMemoization[] = new int[MAX]; 

	public static void main(String[] args) {
		int n = 10;
		
		System.out.println(fib(n));  // Time complexity : O(n), Space complexity : O(1)
		
		System.out.println(fibRecursive(n));
		// for above technique,
		// Time complexity : T(n) = T(n-1) + T(n-2) which is exponential,
		// Space complexity : O(n) if we consider the function call stack size, otherwise O(1),
		// For more clarification: https://stackoverflow.com/questions/28756045/what-is-the-space-complexity-of-a-recursive-fibonacci-algorithm
		
		System.out.println(fibTailRecursive(n, 0, 1));
		// for above technique,
		// Time complexity : O(n),
		// Space complexity : O(n) if we consider the function call stack size, otherwise O(1),

		_initialize(lookupTableForDPMemoization, NIL);
		System.out.println(fibDPMemoization(10));  // Time complexity : O(n), Space complexity : O(n)
		
		System.out.println(fibDPTabulation(n));  // Time complexity : O(n), Space complexity : O(n)
		
		System.out.println(fibUsingFormula(n));  // Time complexity : O(1), Space complexity : O(1)

		System.out.println(fibMatrixMultiplication(n));  // Time complexity : O(n), Space complexity : O(1)
		
		System.out.println(fibOptimizedMatrixMultiplication(n));  // Time complexity : O(log n), Space complexity : O(1)
		
		_initialize(lookupTableForFormulaMemoization, NIL);
		System.out.println(fibFormulaMemoization(10));  // Time complexity : O(n), Space complexity : O(n)

	}

	static int fib(int n) {
		int a = 0;
		int b = 1;
		int c = 0;
		if (n == 0 || n == 1) {
			return n;
		}
		for (int i = 2; i <= n; i++) {
			c = a + b;
			a = b;
			b = c;
		}
		return c;
	}

	static int fibRecursive(int n) {
		if(n == 0 || n ==1)
			return n;
		else {
			int fib = fibRecursive(n - 1) + fibRecursive(n - 2);
			return fib;
		}
	}

	static int fibTailRecursive(int n, int a, int b) {
		if (n == 0) 
			return a; 
		if (n == 1) 
			return b; 
		return fibTailRecursive(n - 1, b, a + b); 
	}

	static void _initialize(int[] lookupTable, int val) {
		for (int i = 0; i < MAX; i++) 
			lookupTable[i] = val;
	}

	static int fibDPMemoization(int n) {
		if (lookupTableForDPMemoization[n] == NIL) {
			if (n <= 1) 
				lookupTableForDPMemoization[n] = n; 
			else
				lookupTableForDPMemoization[n] = fibDPMemoization(n-1) + fibDPMemoization(n-2); 
		} 
		return lookupTableForDPMemoization[n];
	} 

	static int fibDPTabulation(int n) {
		/* Declare an array to store Fibonacci numbers. */
		int f[] = new int[n+2]; // 1 extra to handle case, n = 0 
		int i; 

		/* 0th and 1st number of the series are 0 and 1*/
		f[0] = 0; 
		f[1] = 1; 

		for (i = 2; i <= n; i++) 
		{ 
			/* Add the previous 2 numbers in the series 
		and store it */
			f[i] = f[i-1] + f[i-2]; 
		} 

		return f[n]; 
	} 

	static int fibUsingFormula(int n) { 
		double phi = (Math.sqrt(5) + 1) / 2;
		return (int) Math.round(Math.pow(phi, n) 
				/ Math.sqrt(5)); 
	}

	public static int fibFormulaMemoization(int n) {
		if (lookupTableForFormulaMemoization[n] == NIL) {
			if (n <= 1) {
				lookupTableForFormulaMemoization[n] = n;
			} else {
				int k = (n % 2 == 0) ? (n / 2) : (n + 1) / 2;
				lookupTableForFormulaMemoization[n] = (n % 2 == 0) 
						?
					(fibFormulaMemoization(k) * (2 * fibFormulaMemoization(k - 1) + fibFormulaMemoization(k)))
						:
					(fibFormulaMemoization(k) * fibFormulaMemoization(k) + fibFormulaMemoization(k - 1) * fibFormulaMemoization(k - 1));
			}
		}
		return lookupTableForFormulaMemoization[n];
	}

	static int fibMatrixMultiplication(int n) {
		int F[][] = new int[][]{{1,1},{1,0}}; 
		if (n == 0) 
			return 0; 
		power(F, n-1); 

		return F[0][0]; 
	}

	static void power(int F[][], int n) {
		if( n == 0 || n == 1) 
			return;
		int M[][] = new int[][]{{1,1},{1,0}}; 

		// n - 2 times multiply the matrix to {{1,0},{0,1}} 
		for (int i = 2; i <= n; i++) 
			multiply(F, M); 
	} 

	static void multiply(int F[][], int M[][]) {
		int x = F[0][0]*M[0][0] + F[0][1]*M[1][0]; 
		int y = F[0][0]*M[0][1] + F[0][1]*M[1][1]; 
		int z = F[1][0]*M[0][0] + F[1][1]*M[1][0]; 
		int w = F[1][0]*M[0][1] + F[1][1]*M[1][1]; 

		F[0][0] = x; 
		F[0][1] = y; 
		F[1][0] = z; 
		F[1][1] = w; 
	} 

	static int fibOptimizedMatrixMultiplication(int n) {
		int F[][] = new int[][]{{1,1},{1,0}}; 
		if (n == 0) 
			return 0; 
		optimizedPower(F, n-1); 

		return F[0][0]; 
	}

	static void optimizedPower(int F[][], int n) {
		if( n == 0 || n == 1) 
			return; 
		int M[][] = new int[][]{{1,1},{1,0}}; 

		optimizedPower(F, n/2); 
		multiply(F, F); 

		if (n%2 != 0) 
			multiply(F, M); 
	} 

}
