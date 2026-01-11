package av_stack;

public class RainWaterTrapping {
	
	public static int rainWaterTrapping(int[] arr) {
		int[] maxL = new int[arr.length];
		int[] maxR = new int[arr.length];
		int[] water = new int[arr.length];
		for (int i = arr.length - 1; i >= 0; i--) {
			if (i == arr.length - 1) {
				maxR[i] = arr[i];
			} else {
				if (maxR[i + 1] < arr[i]) {
					maxR[i] = arr[i];
				} else {
					maxR[i] = maxR[i + 1];
				}
			}
		}
		ApplicationStack.printArray(maxR);
		for (int i = 0; i < arr.length; i++) {
			if (i == 0) {
				maxL[i] = arr[i];
			} else {
				if (maxL[i - 1] < arr[i]) {
					maxL[i] = arr[i];
				} else {
					maxL[i] = maxL[i - 1];
				}
			}
		}
		ApplicationStack.printArray(maxL);
		for (int i = 0; i < arr.length; i++) {
			water[i] = Math.min(maxL[i], maxR[i]) - arr[i];
		}
		int totalWater = 0;
		for (int i = 0; i < water.length; i++) {
			totalWater = totalWater + water[i];
		}
		return totalWater;
	}

}
