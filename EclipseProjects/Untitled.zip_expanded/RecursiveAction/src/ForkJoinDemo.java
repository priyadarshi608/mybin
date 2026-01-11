import java.util.concurrent.ForkJoinPool;

public class ForkJoinDemo {
    public static void main(String[] args) {
 
           ForkJoinPool forkJoinPool=new ForkJoinPool();
           long startNanoSec=0;
           long endNanoSec=0;
           
           long[] numberAr=new long[100000];
           for(int i=0;i<100000;i++){
                  numberAr[i]=i;
           }
 
           System.out.print("original array : ");
           for(int i=0;i<10;i++){
                  System.out.print(numberAr[i]+" ");
           }
           
           MyRecursiveAction task=new MyRecursiveAction(0,numberAr.length,numberAr);
 
           
           startNanoSec=System.nanoTime();
           forkJoinPool.invoke(task);
           endNanoSec=System.nanoTime();
           
           System.out.print("\narray after being processed recursively "
                        + "using RecursiveAction : ");
           for(int i=0;i<10;i++){
                  System.out.print(numberAr[i]+" ");
           }
           
           System.out.println();
           System.out.println("Level of Parallelism > "+
                                             forkJoinPool.getParallelism());
           System.out.print("Time taken to complete task : "+
                                             (endNanoSec-startNanoSec)+" nanoSeconds");
           
           
           
    }
}
