package sss.notification.tasks;

import sss.notification.stuffs.MyResource;

public class NotifyTask implements Runnable {
	MyResource myResource;
	
	public NotifyTask(MyResource myResource) {
		this.myResource = myResource;
	}

    @Override
    public void run(){
        myResource.go();
        System.out.println(Thread.currentThread() + " finished Execution");
    }
}
