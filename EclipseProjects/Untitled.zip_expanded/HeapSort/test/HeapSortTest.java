import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class HeapSortTest {
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
		HeapSort sorter = new HeapSort();
		sorter.sort(null);
	}
	
	@Test
	public void testEmpty() {
		HeapSort sorter = new HeapSort();
		sorter.sort(new int[0]);
	}
	
	@Test
	public void testSimpleElement() {
		HeapSort sorter = new HeapSort();
		int[] elements = {5};
		sorter.sort(elements);
	}

	@Test
	public void testSpecial() {
		HeapSort sorter = new HeapSort();
		int[] elements = {-3, 2, 2, 5, 5, 2, 8, 7, -9, 6, 5};
		sorter.sort(elements);
		assertTrue(validate(elements));
	}

	@Test
	public void testHeapSort() {
		for (Integer integer : numbers) {
			System.out.print(integer + ", ");
		}
		System.out.println();
		HeapSort sorter = new HeapSort();
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
