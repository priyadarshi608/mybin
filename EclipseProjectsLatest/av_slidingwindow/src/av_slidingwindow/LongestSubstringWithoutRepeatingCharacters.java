package av_slidingwindow;

import java.util.HashMap;

public class LongestSubstringWithoutRepeatingCharacters {
	
	public static String linearTime(String str) {
		// Creating a set to store the last positions of occurrence
		HashMap<Character, Integer> seen = new HashMap<>();
		int maximum_length = 0, max_substring_start = 0;

		// starting the initial point of window to index 0
		int start = 0;

		for(int end = 0; end < str.length(); end++) {

			// Checking if we have already seen the element or not
			if(seen.containsKey(str.charAt(end))) {
				// If we have seen the element, move the start pointer to position after previous occurrence
				start = Math.max(start, seen.get(str.charAt(end)) + 1);
			}

			// Updating the last seen value of the character
			seen.put(str.charAt(end), end);
			if (maximum_length < (end - start + 1)) {
				maximum_length = end - start + 1;
				max_substring_start = start;
			}
		}
		return str.substring(max_substring_start, max_substring_start + maximum_length);
	}

	public static String slidingWindow(String str) {
		int n = str.length();
		int longestSubstringIndex = 0, length = 0;
		
		for(int i = 0; i < n; i++) {
			
			// Note : Default values in visited are false
			boolean[] visited = new boolean[256];
			
			for(int j = i; j < n; j++) {
				
				// If current character is visited, break the loop
				if (visited[str.charAt(j)] == true) {
					break;
				} else {  // Else update the result if this window is larger, and mark current character as visited.
					if (length < (j - i + 1)) {
						length = j - i + 1;
						longestSubstringIndex = i;
					}
					visited[str.charAt(j)] = true;
				}

			}

			// Remove the first character of previous window
			visited[str.charAt(i)] = false;
		}
		return str.substring(longestSubstringIndex, longestSubstringIndex + length);
	}
	
}
