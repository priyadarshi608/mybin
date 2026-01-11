
public class LPSDynamic_1 {
	
	// A utility function to print a substring str[low..high]
	static void printSubStr( char[] str, int low, int high ) {
		for( int i = low; i <= high; ++i )
			System.out.print(str[i]);
	}

	// This function prints the longest palindrome substring of
	// str[]. It also returns the length of the longest palindrome.
	static int longestPalSubstr( char[] str ) {
		int n = str.length; // get length of input string

		// table[i][j] will be false if substring str[i..j]
		// is not palindrome.
		// Else table[i][j] will be true
		boolean[][] table = new boolean[n][n];
		initializeMatrix(table, false);
		for (int a = 0; a < n; a++) {
			for (int b = 0; b < n; b++) {
				table[a][b] = false;
			}
		}

		// All substrings of length 1 are palindromes
		int maxLength = 1;
		for (int i = 0; i < n; ++i)
			table[i][i] = true;
		printMatrix(table, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
		// check for sub-string of length 2.
		int start = 0;
	    for (int i = 0; i < n-1; ++i) {
	        if (str[i] == str[i+1]) {
	            table[i][i+1] = true;
	            start = i;
	            maxLength = 2;
	        }
	    }
	    printMatrix(table, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    
		// Check for lengths greater than 2. k is length
		// of substring
		for (int cl = 3; cl <= n; ++cl) {
			// Fix the starting index
			for (int i = 0; i < n-cl+1 ; ++i) {
				// Get the ending index of substring from
				// starting index i and length k
				int j = i + cl - 1;

				System.out.println("cl : " + cl);
				System.out.println("i : " + i);
				System.out.println("j : " + j);
	            // checking for sub-string from ith index to
				// jth index iff str[i+1] to str[j-1] is a
				// palindrome
	            if (table[i+1][j-1] && str[i] == str[j]) {
					table[i][j] = true;

					if (cl > maxLength) {
						start = i;
						maxLength = cl;
					}
				}
	            printMatrix(table, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
		}

		System.out.print("Longest palindrome substring is: ");
		printSubStr( str, start, start + maxLength - 1 );
		System.out.println();

		return maxLength; // return length of LPS
	}
	
	// Driver program to test above functions
	public static void main(String[] args) {
//		String testStr = "ab";
//		String testStr = "forgeeksskeegfor";
		String testStr = "dalad";
		char[] str = testStr.toCharArray();
		System.out.println("Length of LongestPalindromicSubstring: " + 
		longestPalSubstr(str));
	}
	
	static void printMatrix(boolean[][] matrix, String delimiter) {
		System.out.println(delimiter);
		int m = matrix.length;
		int n = matrix.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (matrix[i][j] == true) {
					System.out.print("+++++" + " ");
				} else {
					System.out.print(matrix[i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
	
	static void initializeMatrix(boolean[][] matrix, boolean initVal) {
		int m = matrix.length;
		int n = matrix.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = initVal;
			}
		}
	}
	
}
