
public class LPSDynamic_2 {
	// A utility function to print a substring str[low..high]
	static void printSubStr( char[] str, int low, int high ) {
		for( int i = low; i <= high; ++i )
			System.out.print(str[i]);
	}

	// This function prints the longest palindrome substring
	// of str[].
	// It also returns the length of the longest palindrome
	static int longestPalSubstr( char[] str ) {
		int n = str.length; // get length of input string

		// table[i][j] will be false if substring str[i..j]
		// is not palindrome.
		// Else table[i][j] will be true
		boolean[][] table = new boolean[n][n];
		for (int a = 0; a < n; a++) {
			for (int b = 0; b < n; b++) {
				table[a][b] = false;
			}
		}

		// All substrings of length 1 are palindromes
		int maxLength = 1;
		for (int i = 0; i < n; ++i)
			table[i][i] = true;

		int start = 0;

		// Check for lengths greater than 2. k is length
		// of substring
		for (int k = 2; k <= n; ++k) {
			// Fix the starting index
			for (int i = 0; i < n-k+1 ; ++i) {
				// Get the ending index of substring from
				// starting index i and length k
				int j = i + k - 1;

	            if (str[i] == str[i+1] && k == 2) {
					table[i][i+1] = true;
					start = i;
					maxLength = k;
				}
	            
	            // checking for sub-string from ith index to
				// jth index iff str[i+1] to str[j-1] is a
				// palindrome
	            else if (table[i+1][j-1] && str[i] == str[j]) {
					table[i][j] = true;

					if (k > maxLength) {
						start = i;
						maxLength = k;
					}
				}
			}
		}

		System.out.print("Longest palindrome substring is: ");
		printSubStr( str, start, start + maxLength - 1 );
		System.out.println();

		return maxLength; // return length of LPS
	}
	
	// Driver program to test above functions
	public static void main(String[] args) {
		String testStr = "forgeeksskeegfor";
		char[] str = testStr.toCharArray();
		System.out.println("Length of LongestPalindromicSubstring: " + 
		longestPalSubstr(str));
	}
}
