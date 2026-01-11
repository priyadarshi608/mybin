// Java program to rotate an array by d elements 
public class RotateArray {
	
	/*Fuction to get gcd of a and b*/
	int gcd(int a, int b) {
		if (b == 0) 
			return a; 
		else
			return gcd(b, a % b); 
	} 

	/*Function to left rotate arr[] of siz n by d*/
	void leftRotate(int arr[], int d, int n) {
		int i, j, k = -1, temp;
		int g_c_d = gcd(d, n);
		for (i = 0; i < g_c_d; i++) {
			/* move i-th values of blocks */
			temp = arr[i]; 
			j = i;
			while (true) { 
				k = j + d;
				if (k >= n) {
					k = k - n; 
				}
				if (k == i) {
					break; 
				}
				arr[j] = arr[k];
				j = k; 
			}
			arr[j] = temp; 
		} 
	}
	
	/* function to print an array */
	void printArray(int arr[]) {
		for (int i = 0; i < arr.length; i++) 
			System.out.print(arr[i] + " ");
		System.out.println();
	} 

	// Driver program to test above functions 
	public static void main(String[] args) {
		RotateArray rotate = new RotateArray();
		int arr[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}; 
		rotate.leftRotate(arr, 3, 12); 
		rotate.printArray(arr);
	}
	
//	/*Function to left rotate arr[] of siz n by d*/
//	void leftRotate(int arr[], int d, int n) {
//		int i, j, k = -1, temp;
//		int g_c_d = gcd(d, n);
//		for (i = 0; i < g_c_d; i++) {
//			printArray(arr);
//			/* move i-th values of blocks */
//			temp = arr[i]; 
//			j = i;
//			printVars(i, j, k, temp, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//			while (true) { 
//				k = j + d;
//				printVars(i, j, k, temp, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//				if (k >= n) {
//					k = k - n; 
//					printVars(i, j, k, temp, "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
//				}
//				if (k == i) {
//					printVars(i, j, k, temp, "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
//					break; 
//				}
//				System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
//				System.out.println("j :" + j);
//				System.out.println("k :" + k);
//				System.out.println("arr[j] :" + arr[j]);
//				System.out.println("arr[k] :" + arr[k]);
//				System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
//				System.out.println();
//				arr[j] = arr[k];
//				j = k; 
//			}
//			System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
//			System.out.println("temp : " + temp);
//			System.out.println("arr[j] : " + arr[j]);
//			System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
//			arr[j] = temp; 
//		} 
//	}
//	
//	void printVars(int i, int j, int k, int temp, String delimiter) {
//		System.out.println(delimiter);
//		System.out.println("i : " + i);
//		System.out.println("j : " + j);
//		System.out.println("k : " + k);
//		System.out.println("temp :" + temp);
//		System.out.println(delimiter);
//		System.out.println();
//	}
//
} 
