package av_slidingwindow;

public class MaximumSumSubarraySizeK {
	
	public static int maximumSumSubarraySizeK(int[] arr, int k) {
		// k must be greater
        if (arr.length < k) {
           System.out.println("Invalid");
           return -1;
        }
      
        // Compute sum of first window of size k
        int res = 0;
        for (int i=0; i<k; i++) {
            res += arr[i];
        }
      
        // Compute sums of remaining windows by removing first element of previous
        // window and adding last element of current window.
        int curr_sum = res;
        for (int i=k; i< arr.length; i++) {
           curr_sum += arr[i] - arr[i-k];
           res = Math.max(res, curr_sum);
        }
      
        return res;
	}

//	public static int maximumSumSubarraySizeKAV(int[] arr, int k) {
//		int i = 0, j = 0, sum = 0, max_sum = 0;
//		while (j < arr.length) {
//			sum = sum + arr[j];
//			if (j < arr.length && (j - i + 1) < k) {
//				j++;
//			} else if ((j - i + 1) == k) {
//		           sum += arr[j] - arr[j-k];
//		           max_sum = Math.max(sum, max_sum);
//			}
//		}
//        return max_sum;
//	}
}
