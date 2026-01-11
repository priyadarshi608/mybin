public class FibonacciRecursive {
	// recursive declaration of method fibonacci
	public static int fibonacci(int n)  {
		if(n == 0 || n ==1)
			return n;
		else {
			int fib = fibonacci(n - 1) + fibonacci(n - 2);
//			System.out.println(fib);
			return fib;
		}
	}

	public static void main(String[] args) {
		// To print 10 numbers, we will run the loop from 0 to 9.
		for (int i = 0; i <= 9; i++) {
			System.out.println(fibonacci(i));
		}
//		fibonacci(10);
	}
}
