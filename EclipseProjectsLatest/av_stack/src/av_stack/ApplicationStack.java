package av_stack;

public class ApplicationStack {

	public static void main(String[] args) {
//		int[] arr = new int[] {1, 3, 2, 4};
//		int[] ngrResult = NGR.ngr(arr);
//		printArray(ngrResult);
		
//		int[] arr = new int[] {1, 3, 2, 4};
//		int[] nsrResult = NSR.nsr(arr);
//		printArray(nsrResult);
		
//		int[] arr = new int[] {1, 3, 2, 4};
//		int[] nglResult = NGL.ngl(arr);
//		printArray(nglResult);
		
//		int[] arr = new int[] {1, 3, 2, 4};
//		int[] nslResult = NSL.nsl(arr);
//		printArray(nslResult);
		
//		int[] arr = new int[] {100, 80, 60, 70, 60, 75, 85};
//		int[] stockSpanProblemResult = StockSpanProblem.stockSpanProblem(arr);
//		printArray(stockSpanProblemResult);
		
//		int[] arr = new int[] {6, 2, 5, 4, 5, 1, 6};
//		int maxArea = MaximumAreaHistogram.maximumAreaHistogram(arr);
//		System.out.println("maxArea = " + maxArea);
		
//		int[][] arrOfArr = new int[][] {{0, 1, 1, 0}, {1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 0, 0}};
//		int maxArea = MaximumAreaRectangle.maximumAreaRectangle(arrOfArr);
//		System.out.println("maxArea = " + maxArea);
		
//		int[] arr = new int[] {3, 0, 0, 2, 0, 4};
		int[] arr = new int[] {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
		int totalWater = RainWaterTrapping.rainWaterTrapping(arr);
		System.out.println("totalWater = " + totalWater);
	}
	
	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

}