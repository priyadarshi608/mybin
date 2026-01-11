import sss.threadpool.task.Task;
import sss.threadpool.threadpool.ThreadPool;

/**
 * Test ThreadPool.
 */
public class ThreadPoolDemo{
    public static void main(String[] args) throws Exception {
           ThreadPool threadPool=new ThreadPool(2); //create 2 threads in ThreadPool
           for (int i = 1; i <= 7; i++) {
               Runnable task=new Task("task-" + i);
               threadPool.execute(task);
           }
           threadPool.shutdown();
    }
}
