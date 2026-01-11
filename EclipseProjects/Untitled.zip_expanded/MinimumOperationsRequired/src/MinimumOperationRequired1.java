// Java program to find minimum number of steps needed to 
// convert a number x into y 
// with two operations allowed : 
// (1) multiplication with 2 
// (2) subtraction with 1. 
// Constraint: 1<=x<=y<=10000

import java.util.HashSet; 
import java.util.LinkedList; 
import java.util.Set; 

public class MinimumOperationRequired1 {
	
	private static int minOperations(int src, int target) {
		Set<Integer> visited = new HashSet<>(1000); 
		LinkedList<NumbersReachedInfo> queue = new LinkedList<NumbersReachedInfo>(); 

		NumbersReachedInfo node = new NumbersReachedInfo(src, 0); 

		queue.offer(node); 

		while (!queue.isEmpty()) {
			NumbersReachedInfo temp = queue.poll();
			if (!visited.contains(temp.val)) {
				visited.add(temp.val);

				if (temp.val == target) { 
					return temp.steps;
				} 

				int mul = temp.val * 2;
				int sub = temp.val - 1;

				// given constraints 
				if (mul > 0 && mul < 1000) { 
					NumbersReachedInfo nodeMul = new NumbersReachedInfo(mul, temp.steps + 1); 
					queue.offer(nodeMul); 
				}
				
				if (sub > 0 && sub < 1000) { 
					NumbersReachedInfo nodeSub = new NumbersReachedInfo(sub, temp.steps + 1); 
					queue.offer(nodeSub); 
				} 
			}
		} 
		return -1; 
	} 

	// Driver code
	public static void main(String[] args) { 
		int x = 0, y = 7; 
		//int x = 4, y = 7; 
		NumbersReachedInfo src = new NumbersReachedInfo(x, y); 
		System.out.println(minOperations(x, y)); 
	} 
} 
