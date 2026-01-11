import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class SortTest {
	
	private int[] numbers;
	private final static int SIZE = 7;
	private final static int MAX = 20;
	Sort sorter = new RadixSort();
	
	@Before
	public void setUp() throws Exception {
		numbers = new int[SIZE];
		Random generator = new Random();
		for (int i = 0; i < SIZE; i++) {
			numbers[i] = generator.nextInt(MAX);
		}
	}

	@Test
	public void testNull() {
		sorter.sort(null);
	}
	
	@Test
	public void testEmpty() {
		sorter.sort(new int[0]);
	}
	
	@Test
	public void testSimpleElement() {
		int[] elements = {5};
		sorter.sort(elements);
	}

	@Test
	public void testSpecial() {
//		int[] elements = {-3, 2, 2, 5, 5, 2, 8, 7, -9, 6, 5};
//		int[] elements = { 3, 2, 2, 5, 5, 2, 8, 7,  9, 6, 5};    // Only for Radix sort.
//		int[] elements = { 4, 3, 0, 1, 2};                       // Only for Cycle sort.
//		int[] elements = { 4, 3, 4, 1, 2};                       // Only for Cycle sort.
		int[] elements = {185, 3, 9, 481};
		sorter.sort(elements);
		SortingUtility.printArray(elements);
		assertTrue(validate(elements));
	}

	private boolean validate(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return true;
		}
		for (int i = 1; i < arr.length; i++) {
			if (arr[i - 1] > arr[i]) {
				return false;
			}
		}
		return true;
	}
	
}
