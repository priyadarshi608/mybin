import java.util.Arrays;
 
public class CycleSort {
 
    public static void main(String[] args) {
        int[] arr = {4, 3, 0, 1, 2};
 
        System.out.println(Arrays.toString(arr));
 
        int writes = cycleSort(arr);
//        System.out.println(Arrays.toString(arr));
//        System.out.println("writes: " + writes);
    }
 
    static int cycleSort(int[] a) {
        int writes = 0;
 
        for (int cycleStart = 0; cycleStart < a.length - 1; cycleStart++) {
            int val = a[cycleStart];
//            System.out.println("val: " + val);
 
            // count the number of values that are smaller than val
            // since cycleStart
            int pos = cycleStart;
            for (int i = cycleStart + 1; i < a.length; i++)
                if (a[i] < val)
                    pos++;
//            System.out.println("pos: " + pos);
 
            // there aren't any
            if (pos == cycleStart)
                continue;
 
            // skip duplicates
            while (val == a[pos])
                pos++;
 
            // put val into final position
            int tmp = a[pos];
            a[pos] = val;
            val = tmp;
            writes++;
            System.out.println(Arrays.toString(a));
            System.out.println("writes: " + writes);
 
            // repeat as long as we can find values to swap
            // otherwise start new cycle
            while (pos != cycleStart) {
                pos = cycleStart;
                for (int i = cycleStart + 1; i < a.length; i++)
                    if (a[i] < val)
                        pos++;
 
                while (val == a[pos])
                    pos++;
 
                tmp = a[pos];
                a[pos] = val;
                val = tmp;
                writes++;
            }
            System.out.println(Arrays.toString(a));
            System.out.println("writes: " + writes);
        }
        return writes;
    }
}
