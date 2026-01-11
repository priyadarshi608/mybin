
public class PhoneDigitStringsDemo {

	private static char[][] hashTable = new char[][]{
		"".toCharArray(),
		"".toCharArray(),
		"abc".toCharArray(),
		"def".toCharArray(),
		"ghi".toCharArray(),
		"jkl".toCharArray(),
		"mno".toCharArray(),
		"pqrs".toCharArray(),
		"tuv".toCharArray(),
		"wxyz".toCharArray()
		};
	
	private static void  printWordsUtil(int number[], int curr_digit, char output[], int n) {
	    // Base case, if current output word is prepared 
	    if (curr_digit == n) {
	    	System.out.println(output);
	    } else {
		    // Try all 3 possible characters for current digir in number[] 
		    // and recur for remaining digits 
		    for (int i=0; i<(hashTable[number[curr_digit]]).length; i++) {
		        if (number[curr_digit] == 0 || number[curr_digit] == 1) 
		            return; 
		        output[curr_digit] = hashTable[number[curr_digit]][i]; 
		        printWordsUtil(number, curr_digit+1, output, n); 
		    } 
	    }	  
	}
	
	public static void main(String[] args) {
		int number[] = {2, 3, 4};
		char[] result = new char[number.length];
		printWordsUtil(number, 0, result, number.length);
	}

}
