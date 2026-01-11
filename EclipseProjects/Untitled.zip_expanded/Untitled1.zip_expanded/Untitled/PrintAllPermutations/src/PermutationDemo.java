
public class PermutationDemo {

	public static void main(String[] args) throws Exception {
		String str = "ABC";
		StringBuffer strBuf = new StringBuffer(str);
//		StringPermutation.doPerm(strBuf, 0, str.length() - 1);
		StringPermutation.doPermWithRepetition("", str);
	}
}
