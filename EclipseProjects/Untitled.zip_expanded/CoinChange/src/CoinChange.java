import java.util.ArrayList;
import java.util.List;

public class CoinChange {

	int numberOfSolutionsRecursive( int S[], int m, int n ) {
        // If n is 0 then there is 1 solution  
        // (do not include any coin) 
        if (n == 0) 
            return 1; 
          
        // If n is less than 0 then no  
        // solution exists 
        if (n < 0) 
            return 0; 
      
        // If there are no coins and n  
        // is greater than 0, then no 
        // solution exist 
        if (m <=0 && n >= 1) 
            return 0; 
      
        // count is sum of solutions including S[m-1] and excluding S[m-1] 
        return numberOfSolutionsRecursive( S, m - 1, n ) // excluding S[m-1]
        		            +
        	   numberOfSolutionsRecursive( S, m, n-S[m-1] ); // including S[m-1]
    } 
	
	public int numberOfSolutions(int total, int coins[]){
        int temp[][] = new int[coins.length+1][total+1];
        for(int i=0; i <= coins.length; i++){
            temp[i][0] = 1;
        }
        printMatrix(temp, coins.length + 1, total + 1, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for(int i=1; i <= coins.length; i++){
            for(int j=1; j <= total ; j++){
            	System.out.println("i : " + i);
            	System.out.println("j : " + j);
                if(coins[i-1] > j) {
                    temp[i][j] = temp[i-1][j];
                } else {
                    temp[i][j] = temp[i][j-coins[i-1]] + temp[i-1][j];
                }
                printMatrix(temp, coins.length + 1, total + 1, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            }
        }
        return temp[coins.length][total];
    }

    /**
     * Space efficient DP solution
     */
    public int numberOfSolutionsOnSpace(int total, int arr[]){

        int temp[] = new int[total+1];
        printArray(temp, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        temp[0] = 1;
        for(int i=0; i < arr.length; i++) {
            for(int j=1; j <= total; j++) {
            	System.out.println("i : " + i);
            	System.out.println("j : " + j);
                if(j >= arr[i]) {
                    temp[j] += temp[j-arr[i]];
                }
                printArray(temp, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            }
        }
        return temp[total];
    }

    /**
     * This method actually prints all the combination. It takes exponential time.
     */
    public void printCoinChangingSolution(int total,int coins[]){
        List<Integer> result = new ArrayList<>();
        printActualSolution(result, total, coins, 0);
    }
    
    private void printActualSolution(List<Integer> result,int total,int coins[],int pos){
        if(total == 0){
            for(int r : result){
                System.out.print(r + " ");
            }
            System.out.print("\n");
        }
        for(int i=pos; i < coins.length; i++){
            if(total >= coins[i]){
                result.add(coins[i]);
                printActualSolution(result,total-coins[i],coins,i);
                result.remove(result.size()-1);
            }
        }
    }

    public static void main(String args[]){
        CoinChange cc = new CoinChange();
        int total = 15;
        int coins[] = {3,4,6,7,9};
        System.out.println(cc.numberOfSolutions(total, coins));
//        System.out.println(cc.numberOfSolutionsOnSpace(total, coins));
        cc.printCoinChangingSolution(total, coins);
    }
    
	static void printMatrix(int[][] matrix, int m, int n, String delimiter) {
		System.out.println(delimiter);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (matrix[i][j] == 2147483647) {
					System.out.print(" M" + " ");
				} else if (matrix[i][j] >= 0 && matrix[i][j] < 10) {
					System.out.print(" " + matrix[i][j] + " ");
				} else {
					System.out.print(matrix[i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
	
	static void printArray(int[] arr, String delimiter) {
		System.out.println(delimiter);
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
		System.out.println(delimiter);
		System.out.println(delimiter);
	}
}