package sss.notification.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import sss.notification.stuffs.MyResource;

public class WaitTask implements Runnable {
	MyResource myResource;
	
	public WaitTask(MyResource myResource) {
		this.myResource = myResource;
	}

    @Override
    public void run(){
        try {
            myResource.shouldGo();
        } catch (InterruptedException ex) {
            Logger.getLogger(WaitTask.class.getName()).
                   log(Level.SEVERE, null, ex);
        }
        System.out.println(Thread.currentThread().getName() + " finished Execution");
    }
}
