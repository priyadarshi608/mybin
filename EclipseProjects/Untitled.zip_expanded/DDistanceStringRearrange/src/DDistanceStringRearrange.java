import java.util.ArrayList;
import java.util.HashMap;

class CharFreq implements Comparable<CharFreq>{
	Character character;
	Integer frequency;
	
	@Override
	public int compareTo(CharFreq o) {
		return this.frequency - o.frequency;
	}
}

public class DDistanceStringRearrange {

	// The main function that rearranges input string 'str' such that 
	// two same characters become d distance away 
	boolean rearrange(char[] str, int d) {
		// Find length of input string 
		int n = str.length;

		// Create a hashmap to store all characters and their frequencies in str[] 
		HashMap<Character, Integer> charFrequencyMap = new HashMap<>();

		int distinctCharacters = 0; // To store count of distinct characters in str[] 

		// Traverse the input string and store frequencies of all 
		// characters in freq[] array. 
		for (int i = 0; i < n; i++) {
			char x = str[i]; 

			// If this character has occurred first time, increment m 
			if (!charFrequencyMap.containsKey(x)) {
				charFrequencyMap.put(x, 1);
				distinctCharacters++;
			} else {
				int charCount = charFrequencyMap.get(x);
				charFrequencyMap.put(x, ++charCount);
			}
			str[i] = '\0'; // This change is used later 
		} 

		ArrayList<CharFreq> charFreqs = new ArrayList<>();
		for (char ch : charFrequencyMap.keySet()) {
			CharFreq charFreq = new CharFreq();
			charFreq.character = ch;
			charFreq.frequency = charFrequencyMap.get(ch);
			charFreqs.add(charFreq);
		}
		
		// Build a max heap of all characters 
		MaxHeap maxHeap = new MaxHeap(charFreqs);
		maxHeap.buildHeap();

		// Now one by one extract all distinct characters from max heap 
		// and put them back in str[] with the d distance constraint 
		for (int i = 0; i < distinctCharacters; i++) {
			CharFreq x = maxHeap.extractMax();

			// Find the first available position in str[] 
			int p = i; 
			while (str[p] != '\0') 
				p++; 

			// Fill x.c at p, p+d, p+2d, .. p+(f-1)d 
			for (int k = 0; k < x.frequency; k++) {
				// If the index goes beyond size, then string cannot 
				// be rearranged. 
				if (p + d*k >= n) {
					System.out.println("Cannot be rearranged");; 
					return false;
				} 
				str[p + d*k] = x.character;
			} 
		}
		return true;
	} 

}
