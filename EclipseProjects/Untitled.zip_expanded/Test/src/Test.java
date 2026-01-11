
public class Test {
	
	public static void mergeArrays(int[] arr1, int[] arr2) {
		int firstZeroIndex = -1;
		for (int i = 0; i < arr1.length; i++) {
			if (arr1[i] == 0) {
				firstZeroIndex = i;
				break;
			}
		}
		
		int firstPointer = firstZeroIndex - 1;
		int secondPointer = arr2.length - 1;
		
		for (int i = arr1.length - 1; i >= 0; i--) {
			if (firstPointer >= 0 && secondPointer >= 0) {
				if (arr1[firstPointer] >= arr2[secondPointer]) {
					arr1[i] = arr1[firstPointer];
					firstPointer--;
				} else if (arr1[firstPointer] < arr2[secondPointer]) {
					arr1[i] = arr2[secondPointer];
					secondPointer--;
				}
			} else if (firstPointer < 0 && secondPointer >= 0) {
				arr1[i] = arr2[secondPointer];
				secondPointer--;
			} else {				
			}
			
		}
		
	}
	
	

}
