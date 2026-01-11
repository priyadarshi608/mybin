import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class QuickSortTest {
	private int[] numbers;
	private final static int SIZE = 7;
	private final static int MAX = 20;

	@Before
	public void setUp() throws Exception {
		numbers = new int[SIZE];
		Random generator = new Random();
		for (int i = 0; i < SIZE; i++) {
			numbers[i] = generator.nextInt(MAX);
		}
	}

	@Test
	public void testTrue() {
		assertTrue(true);
	}
	
	@Test
	public void testNull() {
		QuickSort sorter = new QuickSort();
		sorter.sort(null);
	}
	
	@Test
	public void testEmpty() {
		QuickSort sorter = new QuickSort();
		sorter.sort(new int[0]);
	}
	
	@Test
	public void testSimpleElement() {
		QuickSort sorter = new QuickSort();
		int[] elements = {5};
		sorter.sort(elements);
	}

	@Test
	public void testSpecial() {
		QuickSort sorter = new QuickSort();
		int[] elements = {-3, 2, 2, 5, 5, 2, 8, 7, -9, 6, 5};
		sorter.sort(elements);
		assertTrue(validate(elements));
	}

	@Test
	public void testQuickSort() {
		for (Integer integer : numbers) {
			System.out.print(integer + ", ");
		}
		System.out.println();
		QuickSort sorter = new QuickSort();
		sorter.sort(numbers);
		for (Integer integer : numbers) {
			System.out.print(integer + ", ");
		}
		System.out.println();
		assertTrue(validate(numbers));
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
