public class LastOccurence {
	public static int lastOccurence(String string, char ch) {
		if (string.charAt(string.length() - 1) == ch)
			return string.length();
		else if (string.length() == 0)
			return -1;
		else {
			return (lastOccurence(string.substring(0, string.length() - 1), ch));
		}
	}
	public static void main(String[] args) {
		String str = "satnohesanntoehstnoe";
		System.out.println(lastOccurence(str, 't'));
	}
}
