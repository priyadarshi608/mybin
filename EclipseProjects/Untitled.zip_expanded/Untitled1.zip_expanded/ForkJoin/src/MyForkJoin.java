import java.util.concurrent.RecursiveTask;

// V V I
@SuppressWarnings("serial")
public class MyForkJoin extends RecursiveTask<Integer> {

  private static final int SEQUENTIAL_THRESHOLD = 5;

  private final int[] data;
  private final int start;
  private final int end;

  public MyForkJoin(int[] data, int start, int end) {
    this.data = data;
    this.start = start;
    this.end = end;
  }
 
  public MyForkJoin(int[] data) {
    this(data, 0, data.length);
  }
 
  @Override
  protected Integer compute() {
    final int length = end - start;
    System.out.println(length);
    if (length < SEQUENTIAL_THRESHOLD) {
      return computeDirectly();
    }
    final int split = length / 2;
    // The order will be
    
    // left.fork();
    // right.compute();
    // left.join();
    
    final MyForkJoin left = new MyForkJoin(data, start, start + split);
    left.fork();
    System.out.println("fork called");
    final MyForkJoin right = new MyForkJoin(data, start + split, end);
    int x = right.compute();
    System.out.println("x = " + x);
    int y = left.join();
    System.out.println("y = " + y);
    return Math.max(x, y);
  }
 
  private Integer computeDirectly() {
    System.out.println(Thread.currentThread() + " computing: " + start + " to " + end);
    int max = Integer.MIN_VALUE;
    for (int i = start; i < end; i++) {
      if (data[i] > max) {
        max = data[i];
      }
    }
    return max;
  }
}
