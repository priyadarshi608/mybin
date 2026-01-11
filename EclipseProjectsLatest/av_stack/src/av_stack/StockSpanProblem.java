/* 
The stock span problem is a financial problem where we have a series
of n daily price quotes for a stock and we need to calculate span of 
stock’s price for all n days. 

The span Si of the stock’s price on a given day i is defined as the maximum 
number of consecutive days just before the given day, for which the price of 
the stock on the current day is less than or equal to its price on the given day. 

For example, if an array of 7 days prices is given as {100, 80, 60, 70, 60, 75, 85}, 
then the span values for corresponding 7 days are {1, 1, 1, 2, 1, 4, 6} 
*/


package av_stack;

import java.util.Stack;
/////////////////////////////// CHILD OF NGL /////////////////////////////////
public class StockSpanProblem {
	
	public static int[] stockSpanProblem(int[] arr) {
		Stack<Pair> stack = new Stack<>();  // pair instead of Integer
		int[] result = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if (stack.isEmpty()) {
				result[i] = -1;
			} else if (stack.peek().element > arr[i]) {  // compare peek().ngl
				result[i] = stack.peek().index;      // store peek().index
			} else if (stack.peek().element <= arr[i]) {  // compare peek().ngl
				while (!stack.isEmpty() && stack.peek().element <= arr[i]) {  // compare peek().ngl
					stack.pop();
				}
				if (stack.isEmpty()) {
					result[i] = -1;
				} else {
					result[i] = stack.peek().index;  // store peek().index
				}
			}
			Pair pair = new Pair(arr[i], i);
			stack.push(pair);
		}
		for (int i = 0; i < result.length; i++) {
			result[i] = i - result[i];  // result[i] = i - result[i];
		}
		return result;
	}

}
