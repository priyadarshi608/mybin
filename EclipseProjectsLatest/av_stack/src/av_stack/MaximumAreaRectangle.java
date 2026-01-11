package av_stack;

public class MaximumAreaRectangle {
	
	public static int maximumAreaRectangle(int[][] arrOfArr) {
		int maxAreaOfRectangle = 0;
		int[] oneDArr = new int[arrOfArr[0].length];
		for (int[] arr : arrOfArr) {
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == 1) {
					oneDArr[i] = oneDArr[i] + 1;
				} else if (arr[i] == 0) {
					oneDArr[i] = 0;
				}
			}
			int maxHistArea = MaximumAreaHistogram.maximumAreaHistogram(oneDArr);
			if (maxAreaOfRectangle < maxHistArea) {
				maxAreaOfRectangle = maxHistArea;
			}
		}
		return maxAreaOfRectangle;
	}

}
