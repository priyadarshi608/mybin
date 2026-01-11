package av_slidingwindow;

import java.util.HashMap;
import java.util.Map;

public class NumberOfOccurrencesOfAnagram {
	
	public static int numberOfOccurrencesOfAnagram(String s, String p) {
		
		if (s == null || p == null || s.length() == 0 || p.length() == 0 || s.length() < p.length()) {
			return 0;
		}
		
		int numberOfAnagram = 0;
		
		Map<Character, Integer> pCharactersMap = new HashMap<>();
		Map<Character, Integer> sCharactersMap = new HashMap<>();
		
		for (char pChar : p.toCharArray()) {
			if (pCharactersMap.containsKey(pChar)) {
				pCharactersMap.put(pChar, pCharactersMap.get(pChar) + 1);
			} else {
				pCharactersMap.put(pChar, 1);
			}
		}
		
		for (int i = 0; i < p.length(); i++) {
			if (sCharactersMap.containsKey(s.charAt(i))) {
				sCharactersMap.put(s.charAt(i), sCharactersMap.get(s.charAt(i)) + 1);
			} else {
				sCharactersMap.put(s.charAt(i), 1);
			}
		}
		
		for (int i = p.length(); i < s.length(); i++) {
			if (compareCharactersMap(pCharactersMap, sCharactersMap)) {
				numberOfAnagram++;
			}
			if (!sCharactersMap.containsKey(s.charAt(i))) {
				sCharactersMap.put(s.charAt(i), 1);
			} else {
				sCharactersMap.put(s.charAt(i), sCharactersMap.get(s.charAt(i)) + 1);
			}
			if (sCharactersMap.get(s.charAt(i - p.length())) > 1) {
				sCharactersMap.put(s.charAt(i - p.length()), sCharactersMap.get(s.charAt(i - p.length())) - 1);
			} else { // sCharactersMap.get(s.charAt(i - p.length())) == 1
				sCharactersMap.remove(s.charAt(i - p.length()));
			}
		}
		
		if (compareCharactersMap(pCharactersMap, sCharactersMap)) { // for last window
			numberOfAnagram++;
		}
		
		return numberOfAnagram;
	}
	
	private static boolean compareCharactersMap(Map<Character, Integer> pCharactersMap, Map<Character, Integer> sCharactersMap) {
		for (char pChar : pCharactersMap.keySet()) {
			if (!sCharactersMap.containsKey(pChar)) {
				return false;
			} else {
				if (pCharactersMap.get(pChar) != sCharactersMap.get(pChar)) {
					return false;
				}
			}
		}
		return true;
	}

}
