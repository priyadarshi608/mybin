import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BucketSort {

	static void bucketSort(double arr[]) {
		int n = arr.length;
		
		// 1) Create n empty buckets
		List<Double>[] buckets = new ArrayList[n];
		for (int i = 0; i < n; i++) {
			buckets[i] = new ArrayList<>();
		}
		
		// 2) Put array elements in different buckets 
		for (int i=0; i<n; i++) {
			int bucketIndex = (int) (n * arr[i]); // Index in bucket 
			buckets[bucketIndex].add(arr[i]);
		}

		// 3) Sort individual buckets 
		for (int i=0; i<n; i++) {
			Collections.sort(buckets[i]);
		}

		// 4) Concatenate all buckets into arr[] 
		int index = 0; 
		for (int i = 0; i < n; i++) 
			for (int j = 0; j < buckets[i].size(); j++) 
			arr[index++] = buckets[i].get(j);
	}

	public static void main(String[] args) {
		double[] arr = new double[] {0.897, 0.565, 0.656, 0.1234, 0.665, 0.3434};
		bucketSort(arr);
		for (double val : arr) {
			System.out.print(val + ", ");
		}
	}
}
