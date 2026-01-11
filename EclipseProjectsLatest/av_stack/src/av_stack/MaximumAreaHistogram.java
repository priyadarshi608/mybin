package av_stack;

import java.util.Stack;

public class MaximumAreaHistogram {
	
	public static int maximumAreaHistogram(int[] arr) {
		int[] nslIndexArray = nslIndex(arr);
		int[] nsrIndexArray = nsrIndex(arr);
		int[] widthArray = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			widthArray[i] = nsrIndexArray[i] - nslIndexArray[i] - 1;
		}
		int[] areaArray = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			areaArray[i] = arr[i] * widthArray[i];
		}
		int maxArea = 0;
		for (int i = 0; i < widthArray.length; i++) {
			if (areaArray[i] > maxArea) {
				maxArea = areaArray[i];
			}
		}
		return maxArea;
	}

	public static int[] nslIndex(int[] arr) {
		int pseudoIndex = -1;
		Stack<Pair> stack = new Stack<>();  // pair instead of Integer
		int[] result = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if (stack.isEmpty()) {
				result[i] = pseudoIndex;
			} else if (stack.peek().element < arr[i]) {  // compare peek().ngl
				result[i] = stack.peek().index;      // store peek().index
			} else if (stack.peek().element >= arr[i]) {  // compare peek().ngl
				while (!stack.isEmpty() && stack.peek().element >= arr[i]) {  // compare peek().ngl
					stack.pop();
				}
				if (stack.isEmpty()) {
					result[i] = pseudoIndex;
				} else {
					result[i] = stack.peek().index;  // store peek().index
				}
			}
			Pair pair = new Pair(arr[i], i);
			stack.push(pair);
		}
		return result;
	}
	
	public static int[] nsrIndex(int[] arr) {
		int pseudoIndex = arr.length;
		Stack<Pair> stack = new Stack<>();  // pair instead of Integer
		int[] result = new int[arr.length];
		for (int i = arr.length - 1; i >= 0 ; i--) {
			if (stack.isEmpty()) {
				result[i] = pseudoIndex;
			} else if (stack.peek().element < arr[i]) {  // compare peek().ngl
				result[i] = stack.peek().index;      // store peek().index
			} else if (stack.peek().element >= arr[i]) {  // compare peek().ngl
				while (!stack.isEmpty() && stack.peek().element >= arr[i]) {  // compare peek().ngl
					stack.pop();
				}
				if (stack.isEmpty()) {
					result[i] = pseudoIndex;
				} else {
					result[i] = stack.peek().index;  // store peek().index
				}
			}
			Pair pair = new Pair(arr[i], i);
			stack.push(pair);
		}
		return result;
	}
	
}
