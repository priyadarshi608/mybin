public class FibonacciIterative {
	static void fib(int numberOfElementsRequired) {
		int a = 0;
		int b = 1;
		int c;
		if (numberOfElementsRequired >= 1) {
			System.out.println(a + " ");
		}
		if (numberOfElementsRequired >= 2) {
			System.out.println(b + " ");
		}
		if (numberOfElementsRequired >= 3) {
			for (int i = 2; i < numberOfElementsRequired; i++) {
				c = a + b;
				a = b;
				b = c;
				System.out.println(c + " ");
			}
		}
	}
	public static void main(String[] args) {
		// To print 10 numbers, we will run the loop from 0 to 9.
		fib(10);
	}
}
