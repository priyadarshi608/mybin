
public class TOH {

	public static void main(String[] args) {
		int nDisks = 15;
		doTowersRecursive(nDisks, 'A', 'B', 'C');
	}

	public static void doTowersRecursive(int topN, char from, char inter, char to) {
		if (topN == 1) {
			System.out.println("Disk 1 from " + from + " to " + to);
		} else {
			doTowersRecursive(topN - 1, from, to, inter);
			System.out.println("Disk " + topN + " from " + from + " to " + to);
			doTowersRecursive(topN - 1, inter, from, to);
		}
	}
}
