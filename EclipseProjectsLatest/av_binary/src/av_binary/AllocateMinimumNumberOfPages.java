package av_binary;

public class AllocateMinimumNumberOfPages {
	
	// method to find minimum pages
	public static int allocateMinimumNumberOfPages(int pagesInBooks[], int numberOfStudents) {
		long sum = 0;
	
		// return -1 if no. of books is less than no. of students
		if (pagesInBooks.length < numberOfStudents)
			return -1;
	
		// Count total number of pages and find max in array
		int maxInArray = Integer.MIN_VALUE;
		for (int i = 0; i < pagesInBooks.length; i++) {
			if (pagesInBooks[i] > maxInArray) {
				maxInArray = pagesInBooks[i];
			}
			sum += pagesInBooks[i];
		}
	
		// initialize start as maxInArray pages and end as total pages
		int start = maxInArray, end = (int) sum;
		int result = Integer.MAX_VALUE;
	
		// traverse until start <= end
		while (start <= end) {
			// check if it is possible to distribute
			// books by using mid is current minimum
			int mid = (start + end) / 2;
			if (isPossible(pagesInBooks, numberOfStudents, mid)) {
				// update result to current distribution
				// as it's the best we have found till now.
				result = mid;
	
				// as we are finding minimum and books are sorted 
				// so reduce end = mid -1 that means
				end = mid - 1;
			}
	
			else
				// if not possible means pages should be
				// increased so update start = mid + 1
				start = mid + 1;
		}
	
		// at-last return minimum no. of pages
		return result;
	}
	
	// Utility method to check if current minimum value is feasible or not.
	private static boolean isPossible(int pagesInBooks[], int numberOfStudents, int curr_min) {
		int studentsRequired = 1;
		int curr_sum = 0;
	
		// iterate over all books
		for (int i = 0; i < pagesInBooks.length; i++) {
			// check if current number of pages are greater than curr_min that
			// means we will get the result after mid no. of pages
			if (pagesInBooks[i] > curr_min)
				return false;
	
			// count how many students are required to distribute curr_min pages
			if (curr_sum + pagesInBooks[i] > curr_min) {
				// increment student count
				studentsRequired++;
	
				// update curr_sum
				curr_sum = pagesInBooks[i];
	
				// if students required becomes greater than given no. of students,return false
				if (studentsRequired > numberOfStudents)
					return false;
			} else {
				curr_sum += pagesInBooks[i];
			}
		}
		return true;
	}
	
}
