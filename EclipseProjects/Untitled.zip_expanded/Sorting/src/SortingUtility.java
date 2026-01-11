
public class SortingUtility {
	
	public static boolean swap(int[] arr, int firstIndex, int secondIndex) {
		if (arr == null || arr.length <= firstIndex || arr.length <= secondIndex) {
			return false;
		}
		int temp = arr[secondIndex];
		arr[secondIndex] = arr[firstIndex];
		arr[firstIndex] = temp;
		return true;
	}
	
	public static void printArray(int[] arr) {
		if (arr == null) {
			System.out.println("array is null");
		} else if (arr.length == 0) {
			System.out.println("array length is zero.");
		} else {
			System.out.print("Elements of the array: ");
			for (int i = 0; i < arr.length; i++) {
				System.out.print(arr[i] + ", ");
			}
			System.out.println();
		}
	}
}
