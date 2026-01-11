package org.jdesktop.application;

import java.util.List;

public abstract interface TaskListener<T, V>
{
  public abstract void doInBackground(TaskEvent<Void> paramTaskEvent);
  
  public abstract void process(TaskEvent<List<V>> paramTaskEvent);
  
  public abstract void succeeded(TaskEvent<T> paramTaskEvent);
  
  public abstract void failed(TaskEvent<Throwable> paramTaskEvent);
  
  public abstract void cancelled(TaskEvent<Void> paramTaskEvent);
  
  public abstract void interrupted(TaskEvent<InterruptedException> paramTaskEvent);
  
  public abstract void finished(TaskEvent<Void> paramTaskEvent);
  
  public static class Adapter<T, V>
    implements TaskListener<T, V>
  {
    public Adapter() {}
    
    public void doInBackground(TaskEvent<Void> paramTaskEvent) {}
    
    public void process(TaskEvent<List<V>> paramTaskEvent) {}
    
    public void succeeded(TaskEvent<T> paramTaskEvent) {}
    
    public void failed(TaskEvent<Throwable> paramTaskEvent) {}
    
    public void cancelled(TaskEvent<Void> paramTaskEvent) {}
    
    public void interrupted(TaskEvent<InterruptedException> paramTaskEvent) {}
    
    public void finished(TaskEvent<Void> paramTaskEvent) {}
  }
}
