package av_slidingwindow;

public class SmallestWindowContainingAllCharacters {
	
	public static String smallestWindowContainingAllCharacters(String string, String pattern) {
		
		char[] stringArray = string.toCharArray();
		char[] patternArray = pattern.toCharArray();
		
		int[] charSetMap = new int[256];

		int length = Integer.MAX_VALUE; // length of ans
		int startOfAns = 0; // starting index of ans
		int countOfRequiredUniqueCharacters = 0; // count of unique characters in pattern
		
		// creating map
		for (int i = 0; i < patternArray.length; i++) {
			if (charSetMap[patternArray[i]] == 0) {
				countOfRequiredUniqueCharacters++;
			}
			charSetMap[patternArray[i]]++;
		}

		// Indexes of Window
		int start = 0, end = 0;

		// Traversing the window
		while (end < stringArray.length) {
		
			// Shifting end towards right
			charSetMap[stringArray[end]]--;
			if (charSetMap[stringArray[end]] == 0) {
				countOfRequiredUniqueCharacters--;
			}

			// Condition matching
			if (countOfRequiredUniqueCharacters == 0) {
				
				while (countOfRequiredUniqueCharacters == 0) {
				
					// Modifying answer
					if (length > end - start + 1) {
						length = Math.min(length, end - start + 1);
						startOfAns = start;
					}
				
					// Sliding start towards right so removing startTh character from the window
					charSetMap[stringArray[start]]++;
					// If after increasing count of startTh character in charSetMap, the count is greater than
					// zero, then we need to increase countOfRequiredUniqueCharacters.
					if (charSetMap[stringArray[start]] > 0) {
						countOfRequiredUniqueCharacters++;
					}
					start++;
				}
			}
			end++;
		}

		if (length != Integer.MAX_VALUE)
			return String.valueOf(stringArray).substring(startOfAns, startOfAns + length);
		else
			return "-1";
	}

}
