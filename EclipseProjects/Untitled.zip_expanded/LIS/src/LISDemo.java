
public class LISDemo {

	public static void main(String[] args) {
		int[] A = { 1, 12, 7, 0, 23, 11, 52, 31, 61, 69, 70, 2 };
		LIS lIS = new LIS();
		int[] lis = lIS.lis(A);
		for (int k = 0; k < lis.length; k++ ) {
			System.out.print(lis[k] + " ");
		}
		System.out.println();
		
//		System.out.println(LIS.lisRecursive(A));
	}

}
