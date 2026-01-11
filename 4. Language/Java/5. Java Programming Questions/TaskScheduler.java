package testing;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
 
public class TaskScheduler extends TimerTask {
 
    @Override
    public void run() {
    	System.out.println("11111");
        System.out.println("Timer task started at:"+new Date());
        completeTask();
    	System.out.println("222222222");
        System.out.println("Timer task finished at:"+new Date());
    }
 
    private void completeTask() {
        try {
            //assuming it takes 20 secs to complete the task
        	System.out.println("333333");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
     
    public static void main(String args[]){
        TimerTask timerTask = new TaskScheduler();
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*100);
        System.out.println("TimerTask started");
        //cancel after sometime
        try {
        	System.out.println("444444");
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        System.out.println("TimerTask cancelled");
        try {
        	System.out.println("555555");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
}