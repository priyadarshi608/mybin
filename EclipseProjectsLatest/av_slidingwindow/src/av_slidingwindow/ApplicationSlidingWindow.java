package av_slidingwindow;

public class ApplicationSlidingWindow {
	
	public static void main(String[] args) {
//		int[] arr = {1, 4, 2, 10, 2, 3, 1, 0, 20};
//		int k = 4;
//		// A Simple Solution is to generate all subarrays of size k, compute their sums and 
//		// finally return the maximum of all sums. The time complexity of this solution is O(n*k).
//		// Following is an efficient solution:
//		int maxSum = MaximumSumSubarraySizeK.maximumSumSubarraySizeK(arr, k);
////		int maxSum = MaximumSumSubarraySizeK.maximumSumSubarraySizeKAV(arr, k);
//		System.out.println("maxSum: " + maxSum);
		
//		int[] arr = {-8, 2, 3, -6, 10};
//		int k = 2;
//		FirstNegativeIntegerInWindowSizeK.firstNegativeIntegerInWindowSizeK(arr, k);
		
////		String text = "forxxorfxdofr";
////		String word = "for";
//		String text = "aabaabaa";
//        String word = "aaba";
//        System.out.print(NumberOfOccurrencesOfAnagram.numberOfOccurrencesOfAnagram(text, word));
		
////		int[] arr = {1, 2, 3, 1, 4, 5, 2, 3, 6};
////		int k = 3;
//		int arr[] = { 12, 1, 78, 90, 57, 89, 56 };
//		int k = 3;
//		int[] maxInSubarrays = MaximumInSubarraysOfSizeK.maximumInSubarraysOfSizeK(arr, k);
//		printArray(maxInSubarrays);
		
////		int[] arr = { 10, 5, 2, 7, 1, 9 };
////		int k = 15;
//		int[] arr = { -5, 8, -14, 2, 4, 12 };
//		int k = -5;
//		int sizeOfSubarray = LongestSubarrayOfSumK.longestSubarrayOfSumK(arr, k);
//		System.out.println("sizeOfSubarray : " + sizeOfSubarray);
		
//		String s = "aabbcc";
//		int k = 3;
//		// Approach 1: If the length of string is n, then there can be n*(n+1)/2 possible substrings. A simple way 
//		// is to generate all the substring and check each one whether it has exactly k unique characters 
//		// or not. If we apply this brute force, it would take O(n2) to generate all substrings and O(n) 
//		// to do a check on each one. Thus overall it would go O(n3). To generate all the substrings, run
//		// two for loops with i, j from 0 to arr.length.
//		// Approach 2: We can further improve this solution by creating a hash table and while generating the
//		// substrings, check the number of unique characters using that hash table. Thus it would improve up to O(n2).
//		// Approach 3: The problem can be solved in O(n). Idea is to maintain a window and add elements to the 
//		// window till it contains less or equal k, update our result if required while doing so. If unique 
//		// elements exceeds than required in window, start removing the elements from left side. Considering 
//		// function “isValid()” takes constant time, time complexity of this solution is O(n).
//		String subString = LongestSubstringWithKUniqueCharacters.longestSubstringWithKUniqueCharacters(s, k);
//		System.out.println("subString: " + subString);
		
//		String str = "aabbcc";
//		// Approach 1: We can consider all substrings one by one and check for each substring whether it
//		// contains all unique characters or not. There will be n*(n+1)/2 substrings. Time complexity O(n^3).
//		// Approach 2: The idea is to use window sliding. Whenever we see repitition, we remove the pervious 
//		// occurrance and slide the window. Time complexity O(n^2).
////		String subString = LongestSubstringWithoutRepeatingCharacters.slidingWindow(str);
//		// Approach 3: linearTime solution. Time Complexity: O(n + d) where n is length of the input string 
//		// and d is number of characters in input string alphabet. For example, if string consists of lowercase 
//		// English characters then value of d is 26. Auxiliary Space: O(d) 
//		String subString = LongestSubstringWithoutRepeatingCharacters.linearTime(str);
//		System.out.println("subString: " + subString);
		
//		// ########################################## PICK TOYS #############################################
//		// A boy goes to a mall with his mother. There are toys on a shelf in line. He wants to pick maximum
//		// number of toys. His mother puts two conditions: first is he has to pick the toys in a row only and
//		// second is he can pick atmost two types of toys. How many maximum number of toys will he be able
//		// to pick. The row of the shelf is as follows:
//		// car car gun animal animal animal gun car
//		String string = "abaccab";
//		String subString = LongestSubstringWithKUniqueCharacters.longestSubstringWithKUniqueCharacters(string, 2);
//		System.out.println("subString: " + subString);
		
		String string = "abaccab";
		String pattern = "cca";
		String subString = SmallestWindowContainingAllCharacters.smallestWindowContainingAllCharacters(string, pattern);
		System.out.println("subString: " + subString);
	}

	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

}
