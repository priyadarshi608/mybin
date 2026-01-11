public class MaximumSumNonAdjacent {
    int FindMaxSum(int arr[], int n) {
        int incl = arr[0];
        int excl = 0; 
        int excl_new; 

        for (int i = 1; i < n; i++) { 
            /* CURRENT MAX EXCLUDING i */
            excl_new = (incl > excl) ? incl : excl; 

            /* CURRENT MAX INCLUDING i */
            incl = excl + arr[i]; 
            excl = excl_new; 
        } 

        /* return max of incl and excl */
        return ((incl > excl) ? incl : excl); 
    } 

    // Driver program to test above functions 
    public static void main(String[] args) {
    	MaximumSumNonAdjacent sum = new MaximumSumNonAdjacent(); 
        int arr[] = new int[]{5, 5, 10, 100, 10, 5}; 
        System.out.println(sum.FindMaxSum(arr, arr.length)); 
    } 
} 
