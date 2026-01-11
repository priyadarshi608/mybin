public class WildcardCharacterMatching {

	static boolean match(String first, String second) { 

		if (first.length() > 0 && first.charAt(0) == '?' && second.length() == 0) 
			return false;

		if (first.length() > 0 && first.charAt(0) == '?' && second.length() > 0)
			return match(first.substring(1), second.substring(1)); 

		if (first.length() > 0 && first.charAt(0) == '*' && second.length() == 0)
			return match(first.substring(1), second);

		if (first.length() > 0 && first.charAt(0) == '*' && second.length() > 0) 
			return match(first.substring(1), second) || match(first, second.substring(1)); 

		if (first.length() == 0 && second.length() == 0) 
			return true;

		if ((first.length() == 0 && second.length() != 0) || (first.length() != 0 && second.length() == 0 )) 
			return false;

		if ((first.length() != 0 && second.length() != 0 && first.charAt(0) == second.charAt(0))) 
			return match(first.substring(1), second.substring(1)); 

		return false; 
	} 

	// A function to run test cases 
	static void test(String first, String second) { 
		if (match(first, second)) 
			System.out.println("Yes"); 
		else
			System.out.println("No"); 
	} 

	public static void main(String[] args) { 
		test("*", ""); // Yes
		test("**", ""); // Yes
		test("**a", "aa"); // Yes
		test("g*ks", "geeks"); // Yes 
		test("ge?ks*", "geeksforgeeks"); // Yes 
		test("abc*bcd", "abcdhghgbcd"); // Yes 
		test("*c*d", "abcd"); // Yes 
		test("*?c*d", "abcd"); // Yes 
		test("*a", ""); // No
		test("?s", ""); // No
		test("g*k", "gee"); // No because 'k' is not in second 
		test("*pqrs", "pqrst"); // No because 't' is not in first 
		test("abc*c?d", "abcd"); // No because second must have 2 instances of 'c' 
	} 

}
