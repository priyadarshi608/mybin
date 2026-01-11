public class MergeSortDemo {
	public static void main(String args[]) {
		int[] myArray = {20, 3, 1, 5, 7, 11, 9, 8, 5, 7, 23, 15};
		MergeSort mergeSort = new MergeSort();
		myArray = mergeSort.sort(myArray);
		for(int i : myArray)
			System.out.println(i);
	}
}
