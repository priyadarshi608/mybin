package av_heap;

public class ApplicationHeap {

	public static void main(String[] args) {
//		int[] arr = new int[] {7, 10, 4, 3, 20, 15};
//		int kthSmallestElement = KthSmallestElement.kthSmallestElement(arr, 3);
//		System.out.println("kthSmallestElement: " + kthSmallestElement);

//		int[] arr = new int[] {7, 10, 4, 3, 20, 15};
//		int[] k_LargestElements = K_LargestElements.k_LargestElement(arr, 3);
//		printArray(k_LargestElements);

//		int[] arr = new int[] {6, 5, 3, 2, 8, 10, 9};
//		Sort_K_Sorted.sort_K_Sorted(arr, 3);
//		printArray(arr);

//		int[] arr = new int[] {5, 6, 7, 8, 9};
//		int[] k_ClosestElements = K_ClosestElements.k_ClosestElements(arr, 3, 7);
//		printArray(k_ClosestElements);
		
//		int[] arr = new int[] {1, 1, 1, 3, 2, 2, 4, 3};
//		int[] k_FrequentElements = K_FrequentNumbers.k_FrequentNumbers(arr, 3);
//		printArray(k_FrequentElements);
		
//		int[] arr = new int[] {1, 1, 1, 3, 2, 2, 4, 3, 3, 2, 2};
//		FrequencySort.frequencySort(arr);
//		printArray(arr);
		
//		int[][] arrOfPoints = new int[][] {{1, 3}, {-2, 2}, {5, 8}, {0, 1}, {0, 0}};
//		int[][] closestPoints = K_ClosestPointsToOrigin.k_ClosestPointsToOrigin(arrOfPoints, 2);
//		printArrayOfArray(closestPoints);
		
////		int[] arr = new int[] {4, 3, 2, 6};
//		int[] arr = new int[] {1, 2, 3, 4, 5};
//		int minimumCost = ConnectRopesWithMinimumCost.connectRopesWithMinimumCost(arr);
//		System.out.println("minimumCost: " + minimumCost);
		
		int[] arr = new int[] {20, 8, 22, 4, 12, 10, 14};
		int sum = SumBetweenK1SmallestAndK2Smallest.sumBetweenK1SmallestAndK2Smallest(arr, 3, 6);
		System.out.println("sum: " + sum);
	}
	
	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

	public static void printArrayOfArray(int[][] arrOfArr) {
		for (int i = 0; i < arrOfArr.length; i++) {
			for (int j = 0; j < arrOfArr[i].length; j++) {
				System.out.print(arrOfArr[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

}