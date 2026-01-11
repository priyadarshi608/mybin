package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

public class TaskService extends AbstractBean
{
  private final String name;
  private final ExecutorService executorService;
  private final List<Task> tasks;
  private final PropertyChangeListener taskPCL;
  
  public TaskService(String paramString, ExecutorService paramExecutorService)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null name");
    }
    if (paramExecutorService == null) {
      throw new IllegalArgumentException("null executorService");
    }
    name = paramString;
    executorService = paramExecutorService;
    tasks = new ArrayList();
    taskPCL = new TaskPCL(null);
  }
  
  public TaskService(String paramString) {
    this(paramString, new ThreadPoolExecutor(3, 10, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue()));
  }
  



  public final String getName()
  {
    return name;
  }
  
  private List<Task> copyTasksList() {
    synchronized (tasks) {
      if (tasks.isEmpty()) {
        return Collections.emptyList();
      }
      
      return new ArrayList(tasks);
    }
  }
  
  private class TaskPCL implements PropertyChangeListener {
    private TaskPCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { String str = paramPropertyChangeEvent.getPropertyName();
      if ("done".equals(str)) {
        Task localTask = (Task)paramPropertyChangeEvent.getSource();
        if (localTask.isDone()) { List localList1;
          List localList2;
          synchronized (tasks) {
            localList1 = TaskService.this.copyTasksList();
            tasks.remove(localTask);
            localTask.removePropertyChangeListener(taskPCL);
            localList2 = TaskService.this.copyTasksList();
          }
          firePropertyChange("tasks", localList1, localList2);
          ??? = localTask.getInputBlocker();
          if (??? != null) {
            ((Task.InputBlocker)???).unblock();
          }
        }
      }
    }
  }
  
  private void maybeBlockTask(Task paramTask) {
    final Task.InputBlocker localInputBlocker = paramTask.getInputBlocker();
    if (localInputBlocker == null) {
      return;
    }
    if (localInputBlocker.getScope() != Task.BlockingScope.NONE) {
      if (SwingUtilities.isEventDispatchThread()) {
        localInputBlocker.block();
      }
      else {
        Runnable local1 = new Runnable() {
          public void run() {
            localInputBlocker.block();
          }
        };
        SwingUtilities.invokeLater(local1);
      }
    }
  }
  
  public void execute(Task paramTask) {
    if (paramTask == null) {
      throw new IllegalArgumentException("null task");
    }
    if ((!paramTask.isPending()) || (paramTask.getTaskService() != null)) {
      throw new IllegalArgumentException("task has already been executed");
    }
    paramTask.setTaskService(this);
    List localList1;
    List localList2;
    synchronized (tasks) {
      localList1 = copyTasksList();
      tasks.add(paramTask);
      localList2 = copyTasksList();
      paramTask.addPropertyChangeListener(taskPCL);
    }
    firePropertyChange("tasks", localList1, localList2);
    maybeBlockTask(paramTask);
    executorService.execute(paramTask);
  }
  
  public List<Task> getTasks() {
    return copyTasksList();
  }
  
  public final void shutdown() {
    executorService.shutdown();
  }
  
  public final List<Runnable> shutdownNow() {
    return executorService.shutdownNow();
  }
  
  public final boolean isShutdown() {
    return executorService.isShutdown();
  }
  
  public final boolean isTerminated() {
    return executorService.isTerminated();
  }
  
  public final boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    return executorService.awaitTermination(paramLong, paramTimeUnit);
  }
}
