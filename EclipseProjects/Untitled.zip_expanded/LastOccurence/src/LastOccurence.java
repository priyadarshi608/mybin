public class LastOccurence {
	public static int lastOccurence(String string, char ch) {
		if (string == null || string.length() == 0) {
			return -1;
		} else {
			if (string.charAt(string.length() - 1) == ch)
				return string.length();
			else if (string.length() == 1)
				return -1;
			else {
				return (lastOccurence(string.substring(0, string.length() - 1), ch));
			}
		}
	}
	public static void main(String[] args) {
		String str = "satknohesanntoehstnoe";
//		String str = "";
		System.out.println(lastOccurence(str, 'k'));
	}
}
