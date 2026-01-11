import java.util.Stack; 

// Java program to find duplicate parenthesis in a 
// balanced expression 
public class DuplicateParenthesis {

	// Function to find duplicate parenthesis in a 
	// balanced expression 
	static boolean findDuplicateparenthesis(String s) { 
		// create a stack of characters 
		Stack<Character> Stack = new Stack<>(); 

		// Iterate through the given expression 
		char[] str = s.toCharArray(); 
		for (char ch : str) {
			// if current character is close parenthesis ')' 
			if (ch != ')') {
				Stack.push(ch);
			} else {
				int elementsInside = 0;
				char top = Stack.pop();
				while (top != '(') { 
					elementsInside++;
					top = Stack.pop();
				}
				if (elementsInside == 0) { 
					return true; 
				} 
			}
		} 

		// No duplicates found 
		return false; 
	} 

	// Driver code 
	public static void main(String[] args) { 

		// input balanced expression 
		String str = "(((a+(b))+(c+d)))"; 

		if (findDuplicateparenthesis(str)) { 
			System.out.println("Duplicate Found "); 
		} else { 
			System.out.println("No Duplicates Found "); 
		} 

	}
	
//	// Function to find duplicate parenthesis in a 
//	// balanced expression 
//	static boolean findDuplicateparenthesis(String s) { 
//		// create a stack of characters 
//		Stack<Character> Stack = new Stack<>(); 
//
//		// Iterate through the given expression 
//		char[] str = s.toCharArray(); 
//		for (char ch : str) {
//			System.out.println("---------------------------------------------------------------------");
//			// if current character is close parenthesis ')' 
//			if (ch == ')') { 
//				// pop character from the stack 
//				char top = Stack.pop();
//				System.out.println("top : " + top);
//				printStack(Stack, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//
//				// stores the number of characters between a 
//				// closing and opening parenthesis 
//				// if this count is less than or equal to 1 
//				// then the brackets are redundant else not 
//				int elementsInside = 0;
//				System.out.println("top above while : " + top);
//				while (top != '(') { 
//					System.out.println("top inside while : " + top);
//					elementsInside++;
//					top = Stack.pop();
//					System.out.println("top : " + top);
//					System.out.println("elementsInside : " + elementsInside);
//					printStack(Stack, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//				}
//				System.out.println("not entered in while loop");
//				if (elementsInside < 1) { 
//					return true; 
//				} 
//			} // push open parenthesis '(', operators and 
//			// operands to stack 
//			else { 
//				Stack.push(ch);
//				printStack(Stack, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//			}
//			System.out.println("=========================================================================");
//		} 
//
//		// No duplicates found 
//		return false; 
//	} 
//
	static void printStack(Stack<Character> s, String delimiter) {
		System.out.println(delimiter);
		Stack<Character> scopy1 = new Stack<>(); 
		Stack<Character> scopy2 = new Stack<>(); 
		while (!s.isEmpty()) {
			Character sInt = s.pop();
//			System.out.print(sInt + " ");
			scopy1.push(sInt);
			scopy2.push(sInt);
		}
		while (!scopy2.isEmpty()) {
			Character sInt = scopy2.pop();
			System.out.print(sInt + " ");
		}
		System.out.println();
		while (!scopy1.isEmpty()) {
			Character scopyInt = scopy1.pop();
			s.push(scopyInt);
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
		System.out.println();
	}
	
} 
