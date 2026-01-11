public class MatrixSpiralBottomRightAntiClockwise {
	static void spiralPrint(int[][] myArr) {
		int m = myArr.length;
		int n = myArr[0].length;
		int upLimit = 0;
		int downLimit = myArr.length - 1;
		int leftLimit = 0;
		int rightLimit = myArr[0].length - 1;

		int row = myArr.length - 1;
		int col = myArr[0].length - 1;

		/////////////////////////////// NOT COMPLETED /////////////////////////
		while (downLimit > upLimit && rightLimit > leftLimit) {
			if (downLimit > upLimit) {
				for (int i = downLimit; i >= upLimit; i--) {
					System.out.println(myArr[i][rightLimit]);
				}
				rightLimit--;
			}
			if (rightLimit > leftLimit) {
				for (int i = rightLimit; i >= leftLimit; i--) {
					System.out.println(myArr[upLimit][i]);
				}
				upLimit++;
			}
			if (downLimit > upLimit) {
				for (int i = upLimit; i <= downLimit; i++) {
					System.out.println(myArr[i][leftLimit]);
				}
				leftLimit++;
			}
			if (rightLimit > leftLimit) {
				for (int i = leftLimit; i <= rightLimit; i++) {
					System.out.println(myArr[downLimit][i]);
				}
				downLimit--;
			}
			System.out.println("upLimit: " + upLimit);
			System.out.println("downLimit: " + downLimit);
			System.out.println("leftLimit: " + leftLimit);
			System.out.println("rightLimit: " + rightLimit);
		}
	}

	/* Driver program to test above functions */
	public static void main(String[] args) {
		int[][] a = { {1,  2,  3,  4,  5,  6},
				{7,  8,  9,  10, 11, 12},
				{13, 14, 15, 16, 17, 18}
		};
		spiralPrint(a);
	}
}
