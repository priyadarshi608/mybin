import java.util.Stack;

public class TowerOfHanoiRecursive {
	public static void main(String args[]) {
		Stack<Integer> stk1 = new Stack<>();
		Stack<Integer> stk2 = new Stack<>();
		Stack<Integer> stk3 = new Stack<>();
		for(int i=5;i>0;i--){
			stk1.push(i);
		}
		TOHR(stk1.size(), stk1, stk2, stk3);
		System.out.println(stk1.toString());
		System.out.println(stk2.toString());
		System.out.println(stk3.toString());
	}
	public static void TOHR(int topN, Stack<Integer> from, Stack<Integer> inter, Stack<Integer> to) {
		if (topN == 1) {
			to.push(from.pop());
			return;
		}
		else {
			TOHR(topN - 1, from, to, inter);
			to.push(from.pop());
			TOHR(topN - 1, inter, from, to);
		}
	}
}
