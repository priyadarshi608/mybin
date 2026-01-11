public class LIS {

	static int max_ref;

	static int lisRecursive(int[] arr) {
		int n = arr.length;
		// The max variable holds the result 
		max_ref = 1; 

		// The function _lis() stores its result in max 
		_lis( arr, n); 

		// returns max 
		return max_ref;
	}

	/* To make use of recursive calls, this function must return 
	   two things:
	   1) Length of LIS ending with element arr[n-1]. We use 
	      max_ending_here for this purpose 
	   2) Overall maximum as the LIS may end with an element 
	      before arr[n-1] max_ref is used this purpose. 
	   The value of LIS of full array of size n is stored in 
	 *max_ref which is our final result */
	static int _lis(int arr[], int n) {
		// base case 
		if (n == 1) 
			return 1; 

		// 'max_ending_here' is length of LIS ending with arr[n-1] 
		int res, max_ending_here = 1; 

		/* Recursively get all LIS ending with arr[0], arr[1] ... 
	           arr[n-2]. If   arr[i-1] is smaller than arr[n-1], and 
	           max ending with arr[n-1] needs to be updated, then 
	           update it */
		for (int i = 1; i < n; i++) {
			res = _lis(arr, i); 
			if (arr[i-1] < arr[n-1] && res + 1 > max_ending_here) 
				max_ending_here = res + 1; 
		} 

		// Compare max_ending_here with the overall max. And 
		// update the overall max if needed 
		if (max_ref < max_ending_here) 
			max_ref = max_ending_here;

		// Return length of LIS ending with arr[n-1]
		return max_ending_here;
	} 

	public int[] lis(int[] arr) {
		int n = arr.length;
		int lis[] = new int[n];

		/* Initialize LIS values for all indexes */
		for (int i = 0; i < n; i++ ) {
			lis[i] = 1;
		}

		/* Compute optimized LIS values in bottom up manner */
		for (int i = 1; i < n; i++ ) {
			for (int j = 0; j < i; j++ ) {
				if ( arr[i] > arr[j] && lis[i] < lis[j] + 1) {
					lis[i] = lis[j] + 1;
				}
			}
		}

		/* Pick maximum of all LIS values */
		int result = -1;
		int index = -1;
		for (int i = 0; i < n; i++ ) {
			if ( result < lis[i] ) {
				result = lis[i];
				index = i;
			}
		}
		//		for (int k = 0; k < lis.length; k++ ) {
		//			System.out.print(lis[k] + " ");
		//		}
		//		System.out.println();
		//		System.out.println("result: " + result);
		//		System.out.println("index: " + index);

		int[] lisResult = lisUtil(arr, lis, index);

		return lisResult;
	}

	int[] lisUtil(int[] arr, int[] lis, int index) {
//		System.out.println("index: " + index);
//		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//		for (int i = 0; i < arr.length; i++) {
//			System.out.print(arr[i] + ", ");
//		}
//		System.out.println();
//		System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//		System.out.println();
//		System.out.println("ccccccccccccccccccccccccccccccccccccccccccccccccccccc");
//		for (int i = 0; i < lis.length; i++) {
//			System.out.print(lis[i] + ", ");
//		}
//		System.out.println();
//		System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddd");
//		System.out.println();
		
		int result = lis[index];
		// Start moving backwards from the end and print the result
		int[] lisResult = new int[result];
		int res = result - 1;

		lisResult[res] = arr[index];
		for (int i = index-1; i >= 0; i--) {
			if(lis[i] == res){
				res--;
				lisResult[res] = arr[i];
			}
		}
		return lisResult;
	}

}
