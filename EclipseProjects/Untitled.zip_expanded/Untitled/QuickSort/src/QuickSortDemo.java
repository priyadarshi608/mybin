import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuickSortDemo {
	public static void main(String[] argss) {
		int[] numbers = {43, 11, 23, 25, 57, 16, 33, 19, 58};
		QuickSort quickSort = new QuickSort();
		quickSort.sort(numbers);
	}
}
