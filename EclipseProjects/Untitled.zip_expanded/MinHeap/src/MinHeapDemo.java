import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinHeapDemo {

	public static void main(String[] arg) {
		List<Integer> list = Arrays.asList(5, 3, 17, 10, 84, 19, 6, 22, 9);
		ArrayList<Integer> items = new ArrayList<>(list);
		MinHeap minHeap = new MinHeap(items);
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
		System.out.println(minHeap.extractMin());
	} 

}
