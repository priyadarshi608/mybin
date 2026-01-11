import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
 
class MyTimerTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Timer task started at:"+new Date());
        completeTask();
        System.out.println("Timer task finished at:"+new Date());
    }
    private void completeTask() {
        try {
            //assuming it takes 2 secs to complete the task
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
