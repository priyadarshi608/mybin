import java.util.Arrays;

public class CycleSort implements Sort {
	
	public int[] sort(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return arr;
		}
		cycleSort(arr);
		return arr;
	}
	
	// To understand the running of the program, run the commented method below it
    int cycleSort(int[] arr) {
        int writes = 0;
 
        for (int cycleStart = 0; cycleStart < arr.length - 1; cycleStart++) {
            int val = arr[cycleStart]; 
            int pos = cycleStart;
            
            for (int i = cycleStart + 1; i < arr.length; i++) {
                if (arr[i] < val) {
                    pos++;
                }
            }
 
            // there aren't any
            if (pos == cycleStart)
                continue;
 
            // skip duplicates
            while (val == arr[pos])
                pos++;
 
            // put val into final position
            int tmp = arr[pos];
            arr[pos] = val;
            val = tmp;
            writes++;
 
            // repeat as long as we can find values to swap
            // otherwise start new cycle
            while (pos != cycleStart) {
                pos = cycleStart;
                for (int i = cycleStart + 1; i < arr.length; i++) {
                    if (arr[i] < val) {
                        pos++;
                    }
                }
                
                while (val == arr[pos])
                    pos++;
 
                tmp = arr[pos];
                arr[pos] = val;
                val = tmp;
                writes++;
            }
        }
        return writes;
    }
    
//    int cycleSort(int[] arr) {
//        int writes = 0;
// 
//        for (int cycleStart = 0; cycleStart < arr.length - 1; cycleStart++) {
//            System.out.println("aaaaa cycleStart: " + cycleStart);
//            System.out.println("aaaaa: " + Arrays.toString(arr));
//            int val = arr[cycleStart];
// 
//            // count the number of values that are smaller than val
//            // since cycleStart
//            int pos = cycleStart;
//            System.out.println("aaaaa val: " + val);
//            System.out.println("aaaaa pos: " + pos);
//            for (int i = cycleStart + 1; i < arr.length; i++) {
//                if (arr[i] < val) {
//                    pos++;
//                }
//            }
//            System.out.println("bbbbb cycleStart: " + cycleStart);
//            System.out.println("bbbbb: " + Arrays.toString(arr));
//            System.out.println("bbbbb val: " + val);
//            System.out.println("bbbbb pos: " + pos);
// 
//            // there aren't any
//            if (pos == cycleStart)
//                continue;
// 
//            // skip duplicates
//            while (val == arr[pos])
//                pos++;
//            System.out.println("ccccc cycleStart: " + cycleStart);
//            System.out.println("ccccc: " + Arrays.toString(arr));
//            System.out.println("ccccc val: " + val);
//            System.out.println("ccccc pos: " + pos);
// 
//            // put val into final position
//            int tmp = arr[pos];
//            arr[pos] = val;
//            val = tmp;
//            writes++;
//            System.out.println("ddddd cycleStart: " + cycleStart);
//            System.out.println("ddddd: " + Arrays.toString(arr));
//            System.out.println("ddddd val: " + val);
//            System.out.println("ddddd pos: " + pos);
//            System.out.println("writes: " + writes);
// 
//            // repeat as long as we can find values to swap
//            // otherwise start new cycle
//            while (pos != cycleStart) {
//                System.out.println("eeeee cycleStart: " + cycleStart);
//                System.out.println("eeeee: " + Arrays.toString(arr));
//                System.out.println("eeeee val: " + val);
//                System.out.println("eeeee pos: " + pos);
//                pos = cycleStart;
//                for (int i = cycleStart + 1; i < arr.length; i++) {
//                    if (arr[i] < val) {
//                        pos++;
//                    }
//                }
//                System.out.println("fffff cycleStart: " + cycleStart);
//                System.out.println("fffff: " + Arrays.toString(arr));
//                System.out.println("fffff val: " + val);
//                System.out.println("fffff pos: " + pos);
//                
//                while (val == arr[pos])
//                    pos++;
//                System.out.println("ggggg cycleStart: " + cycleStart);
//                System.out.println("ggggg: " + Arrays.toString(arr));
//                System.out.println("ggggg val: " + val);
//                System.out.println("ggggg pos: " + pos);
// 
//                tmp = arr[pos];
//                arr[pos] = val;
//                val = tmp;
//                writes++;
//                System.out.println("hhhhh cycleStart: " + cycleStart);
//                System.out.println("hhhhh: " + Arrays.toString(arr));
//                System.out.println("hhhhh val: " + val);
//                System.out.println("hhhhh pos: " + pos);
//                System.out.println("writes: " + writes);
//            }
//        }
//        return writes;
//    }
    
}
