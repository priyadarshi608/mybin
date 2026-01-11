package av_binary;

public class SearchIn2DSortedArray {
	
	public static Pair searchIn2DSortedArray(int[][] arr, int value) {
		int i = 0;
		int j = arr[0].length - 1;
		while (i < arr.length && j >= 0) {
			if (arr[i][j] == value) {
				return new Pair(i, j);
			} else if (arr[i][j] > value) {
				j--;
			} else if (arr[i][j] < value) {
				i++;
			}
		}
		return new Pair(-1, -1);
	}

}
