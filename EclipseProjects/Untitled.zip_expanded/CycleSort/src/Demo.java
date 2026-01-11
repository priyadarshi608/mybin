import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        int[] arr = {4, 3, 0, 1, 2};
 
        System.out.println(Arrays.toString(arr));

        CycleSort cycleSort = new CycleSort();
        int writes = cycleSort.sort(arr);
//        System.out.println(Arrays.toString(arr));
//        System.out.println("writes: " + writes);
    }
 
}
