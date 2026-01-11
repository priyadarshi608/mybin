
public class DDistanceStringRearrangeDemo {

	public static void main(String[] args) {
		char str[] = "aaa".toCharArray();
		DDistanceStringRearrange stringRearrange = new DDistanceStringRearrange();
		boolean canBeRearranged = stringRearrange.rearrange(str, 2);
		if (canBeRearranged) {
			System.out.println(str);
		}
	}

}
