public class TextJustification {

    public String justify(String words[], int width) {
        
        int cost[][] = new int[words.length][words.length];
        
        //next 2 for loop is used to calculate cost of putting words from
        //i to j in one line. If words don't fit in one line then we put
        //Integer.MAX_VALUE there.
        for(int i=0 ; i < words.length; i++){
            cost[i][i] = width - words[i].length();
            for(int j=i+1; j < words.length; j++){
                cost[i][j] = cost[i][j-1] - words[j].length() - 1;
            }
        }
        
        for(int i=0; i < words.length; i++){
            for(int j=i; j < words.length; j++){
                if(cost[i][j] < 0){
                    cost[i][j] = Integer.MAX_VALUE;
                }else{
                    cost[i][j] = (int)Math.pow(cost[i][j], 2);
                }
            }
        }
        
        printMatrix(cost, cost.length, cost.length, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        //minCost from i to len is found by trying
        //j between i to len and checking which
        //one has min value
        int minCost[] = new int[words.length];
        int result[] = new int[words.length];
        for(int i = words.length-1; i >= 0 ; i--){
            minCost[i] = cost[i][words.length-1];
            result[i] = words.length;
            for(int j=words.length-1; j > i; j--){
                if(cost[i][j-1] == Integer.MAX_VALUE){
                    continue;
                }
                if(minCost[i] > minCost[j] + cost[i][j-1]){
                    minCost[i] = minCost[j] + cost[i][j-1];
                    result[i] = j;
                }
            }
        }
        int i = 0;
        int j;
        
        System.out.println("Minimum cost is " + minCost[0]);
        System.out.println("\n");
        //finally put all words with new line added in 
        //string buffer and print it.
        StringBuilder builder = new StringBuilder();
        do{
            j = result[i];
            for(int k=i; k < j; k++){
                builder.append(words[k] + " ");
            }
            builder.append("\n");
            i = j;
        }while(j < words.length);
        
        return builder.toString();
    }
    
    public static void main(String args[]){
        String words1[] = {"Tushar","likes","to","write","code","at", "free", "time"};
        TextJustification awl = new TextJustification();
        System.out.println(awl.justify(words1, 12));
    }
    
//    public String justify(String words[], int width) {
//        
//        int cost[][] = new int[words.length][words.length];
//        
//        //next 2 for loop is used to calculate cost of putting words from
//        //i to j in one line. If words don't fit in one line then we put
//        //Integer.MAX_VALUE there.
//        for(int i=0 ; i < words.length; i++){
//            cost[i][i] = width - words[i].length();
//            for(int j=i+1; j < words.length; j++){
//                cost[i][j] = cost[i][j-1] - words[j].length() - 1;
//            }
//        }
//        
//        for(int i=0; i < words.length; i++){
//            for(int j=i; j < words.length; j++){
//                if(cost[i][j] < 0){
//                    cost[i][j] = Integer.MAX_VALUE;
//                }else{
//                    cost[i][j] = (int)Math.pow(cost[i][j], 2);
//                }
//            }
//        }
//        
//        printMatrix(cost, cost.length, cost.length, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        
//        //minCost from i to len is found by trying
//        //j between i to len and checking which
//        //one has min value
//        int minCost[] = new int[words.length];
//        int result[] = new int[words.length];
//        for(int i = words.length-1; i >= 0 ; i--){
//            minCost[i] = cost[i][words.length-1];
//            result[i] = words.length;
//            System.out.println("i : " + i);
//            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            printArray(minCost);
//            printArray(result);
//            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            for(int j=words.length-1; j > i; j--){
//                System.out.println("i : " + i);
//            	System.out.println("j : " + j);
//            	System.out.println("cost[i][j-1] : " + cost[i][j-1]);
//                if(cost[i][j-1] == Integer.MAX_VALUE){
//                    continue;
//                }
//                System.out.println("minCost[i] : " + minCost[i]);
//                System.out.println("minCost[j] : " + minCost[j]);
//                System.out.println("cost[i][j-1] : " + cost[i][j-1]);
//                if(minCost[i] > minCost[j] + cost[i][j-1]){
//                    minCost[i] = minCost[j] + cost[i][j-1];
//                    result[i] = j;
//                }
//                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//                printArray(minCost);
//                printArray(result);
//                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//            }
//        }
//        int i = 0;
//        int j;
//        
//        System.out.println("Minimum cost is " + minCost[0]);
//        System.out.println("\n");
//        //finally put all words with new line added in 
//        //string buffer and print it.
//        StringBuilder builder = new StringBuilder();
//        do{
//            j = result[i];
//            for(int k=i; k < j; k++){
//                builder.append(words[k] + " ");
//            }
//            builder.append("\n");
//            i = j;
//        }while(j < words.length);
//        
//        return builder.toString();
//    }
//    
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
	
	static void printArray(int[] arr) {
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}
	
}
