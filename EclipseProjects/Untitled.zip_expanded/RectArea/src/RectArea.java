//Java program to find maximum rectangular area in linear time 

import java.util.Stack; 

public class RectArea {
	
	static int getMaxArea(int hist[], int n) {
		Stack<Integer> s = new Stack<>(); 
		
		int max_area = 0; // Initialize max area 
		int top; // To store top of stack 
		int area_with_top; // To store area with top bar as the smallest bar 
	
		int i = 0; 
		while (i < n) {
			if (s.empty() || hist[s.peek()] <= hist[i]) {
				s.push(i++);
			} else {
				top = s.pop();

				area_with_top = hist[top] * (s.empty() ? i : i - s.peek() - 1);
	
				// update max area, if needed 
				if (max_area < area_with_top) 
					max_area = area_with_top; 
			}
		}
	
		while (s.empty() == false) {
			top = s.pop();
			area_with_top = hist[top] * (s.empty() ? i : i - s.peek() - 1); 
	
			if (max_area < area_with_top) 
				max_area = area_with_top; 
		}
	
		return max_area; 
	}
	
	// Driver program to test above function 
	public static void main(String[] args) {
		int hist[] = { 6, 2, 5, 4, 5, 1, 6 }; 
		System.out.println("Maximum area is " + getMaxArea(hist, hist.length)); 
	}
	
//	// The main function to find the maximum rectangular area under given 
//	// histogram with n bars 
//	static int getMaxArea(int hist[], int n) {
//		// Create an empty stack. The stack holds indexes of hist[] array 
//		// The bars stored in stack are always in increasing order of their 
//		// heights. 
//		Stack<Integer> s = new Stack<>(); 
//		
//		int max_area = 0; // Initialize max area 
//		int top; // To store top of stack 
//		int area_with_top; // To store area with top bar as the smallest bar 
//	
//		// Run through all bars of given histogram 
//		int i = 0; 
//		while (i < n) {
//			System.out.println("i : " + i);
//			System.out.println("hist[" + i + "] : " + hist[i]);
//			if (!s.isEmpty()) {
//				System.out.println("s.peek() : " + s.peek());
//				System.out.println("s.peek() : " + s.peek());
//			} else {
//				System.out.println("s is empty...........");
//			}
//			// If this bar is higher than the bar on top stack, push it to stack 
//			if (s.empty() || hist[s.peek()] <= hist[i]) {
//				s.push(i++);
//				printStack(s, "1111111111111111111111111111111111111111111111111111111111111");
//			}
//	
//			// If this bar is lower than top of stack, then calculate area of rectangle 
//			// with stack top as the smallest (or minimum height) bar. 'i' is 
//			// 'right index' for the top and element before top in stack is 'left index' 
//			else {
//				top = s.peek(); // store the top index 
//				System.out.println("top : " + top);
//				s.pop(); // pop the top 
//				printStack(s, "2222222222222222222222222222222222222222222222222222222222222");
//
//				System.out.println("hist[top] : " + hist[top]);
//				System.out.println("i : " + i);
//				if (!s.isEmpty()) {
//					System.out.println("s.peek() : " + s.peek());
//					System.out.println("i - s.peek() - 1 : " + (i - s.peek() - 1));
//				}
//				// Calculate the area with hist[top] stack as smallest bar 
//				area_with_top = hist[top] * (s.empty() ? i : i - s.peek() - 1);
//				System.out.println("area_with_top : " + area_with_top);
//				System.out.println("max_area : " + max_area);
//	
//				// update max area, if needed 
//				if (max_area < area_with_top) 
//					max_area = area_with_top; 
//				System.out.println("max_area : " + max_area);
//				System.out.println("333333333333333333333333333333333333333333333333333333333333333");
//			}
//			printStack(s, "444444444444444444444444444444444444444444444444444444444444444444");
//		}
//		printStack(s, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//	
//		// Now pop the remaining bars from stack and calculate area with every 
//		// popped bar as the smallest bar 
//		while (s.empty() == false) {
//			top = s.peek(); 
//			s.pop(); 
//			area_with_top = hist[top] * (s.empty() ? i : i - s.peek() - 1); 
//	
//			if (max_area < area_with_top) 
//				max_area = area_with_top; 
//		}
//		printStack(s, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//	
//		return max_area; 
//
//	}
//	
	static void printStack(Stack<Integer> s, String delimiter) {
		System.out.println(delimiter);
		Stack<Integer> scopy1 = new Stack<>(); 
		Stack<Integer> scopy2 = new Stack<>(); 
		while (!s.isEmpty()) {
			Integer sInt = s.pop();
//			System.out.print(sInt + " ");
			scopy1.push(sInt);
			scopy2.push(sInt);
		}
		while (!scopy2.isEmpty()) {
			Integer sInt = scopy2.pop();
			System.out.print(sInt + " ");
		}
		System.out.println();
		while (!scopy1.isEmpty()) {
			Integer scopyInt = scopy1.pop();
			s.push(scopyInt);
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
		System.out.println();
	}
	
} 
