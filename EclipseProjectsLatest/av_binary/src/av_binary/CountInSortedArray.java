package av_binary;

public class CountInSortedArray {
	
	public static int countInSortedArray(int[] arr, int val) {
		int firstOccurrenceIndex = FirstOccurrence.search(arr, val);
		int lastOccurrenceIndex = LastOccurrence.search(arr, val);
		int count = lastOccurrenceIndex - firstOccurrenceIndex + 1;
		return count;
	}
}
