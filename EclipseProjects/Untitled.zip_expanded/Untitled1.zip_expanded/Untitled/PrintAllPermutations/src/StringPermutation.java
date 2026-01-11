public class StringPermutation {

	public static void doPerm(StringBuffer str, int l, int r) {
		if(l == r) {
			System.out.println(str);
		} else {
			for (int i = l; i <= r; i++) {
				swap(str, l, i);
				doPerm(str, l + 1, r);
				swap(str, l, i); // backtrack
			}
		}
	}

	private  static void swap(StringBuffer str, int pos1, int pos2){
		char t1 = str.charAt(pos1);
		str.setCharAt(pos1, str.charAt(pos2));
		str.setCharAt(pos2, t1);
	}

	static void doPermWithRepetition(String prefix, String str)
	{
		if(prefix.length()==str.length()) {
			System.out.println(prefix); return; 
		}

		for(int i=0; i<str.length(); i++)
			doPermWithRepetition(prefix + (str.charAt(i)), str);
	}
}
