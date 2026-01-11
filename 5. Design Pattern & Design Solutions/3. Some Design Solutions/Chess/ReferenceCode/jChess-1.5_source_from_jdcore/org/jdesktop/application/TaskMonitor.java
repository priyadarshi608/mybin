package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.swingworker.SwingWorker.StateValue;























































public class TaskMonitor
  extends AbstractBean
{
  private final PropertyChangeListener applicationPCL;
  private final PropertyChangeListener taskServicePCL;
  private final PropertyChangeListener taskPCL;
  private final LinkedList<Task> taskQueue;
  private boolean autoUpdateForegroundTask = true;
  private Task foregroundTask = null;
  


  public TaskMonitor(ApplicationContext paramApplicationContext)
  {
    applicationPCL = new ApplicationPCL(null);
    taskServicePCL = new TaskServicePCL(null);
    taskPCL = new TaskPCL(null);
    taskQueue = new LinkedList();
    paramApplicationContext.addPropertyChangeListener(applicationPCL);
    for (TaskService localTaskService : paramApplicationContext.getTaskServices()) {
      localTaskService.addPropertyChangeListener(taskServicePCL);
    }
  }
  












  public void setForegroundTask(Task paramTask)
  {
    Task localTask1 = foregroundTask;
    if (localTask1 != null) {
      localTask1.removePropertyChangeListener(taskPCL);
    }
    foregroundTask = paramTask;
    Task localTask2 = foregroundTask;
    if (localTask2 != null) {
      localTask2.addPropertyChangeListener(taskPCL);
    }
    firePropertyChange("foregroundTask", localTask1, localTask2);
  }
  







  public Task getForegroundTask()
  {
    return foregroundTask;
  }
  










  public boolean getAutoUpdateForegroundTask()
  {
    return autoUpdateForegroundTask;
  }
  










  public void setAutoUpdateForegroundTask(boolean paramBoolean)
  {
    boolean bool = autoUpdateForegroundTask;
    autoUpdateForegroundTask = paramBoolean;
    firePropertyChange("autoUpdateForegroundTask", Boolean.valueOf(bool), Boolean.valueOf(autoUpdateForegroundTask));
  }
  
  private List<Task> copyTaskQueue() {
    synchronized (taskQueue) {
      if (taskQueue.isEmpty()) {
        return Collections.emptyList();
      }
      
      return new ArrayList(taskQueue);
    }
  }
  









  public List<Task> getTasks()
  {
    return copyTaskQueue();
  }
  



  private void updateTasks(List<Task> paramList1, List<Task> paramList2)
  {
    int i = 0;
    List localList = copyTaskQueue();
    
    for (Iterator localIterator = paramList1.iterator(); localIterator.hasNext();) { localObject = (Task)localIterator.next();
      if ((!paramList2.contains(localObject)) && 
        (taskQueue.remove(localObject))) {
        i = 1;
      }
    }
    
    Object localObject;
    for (localIterator = paramList2.iterator(); localIterator.hasNext();) { localObject = (Task)localIterator.next();
      if (!taskQueue.contains(localObject)) {
        taskQueue.addLast(localObject);
        i = 1;
      }
    }
    
    localIterator = taskQueue.iterator();
    while (localIterator.hasNext()) {
      localObject = (Task)localIterator.next();
      if (((Task)localObject).isDone()) {
        localIterator.remove();
        i = 1;
      }
    }
    
    if (i != 0) {
      localObject = copyTaskQueue();
      firePropertyChange("tasks", localList, localObject);
    }
    
    if ((autoUpdateForegroundTask) && (getForegroundTask() == null)) {
      setForegroundTask(taskQueue.isEmpty() ? null : (Task)taskQueue.getLast());
    }
  }
  

  private class ApplicationPCL
    implements PropertyChangeListener
  {
    private ApplicationPCL() {}
    

    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Iterator localIterator; TaskService localTaskService; if ("taskServices".equals(str)) {
        List localList1 = (List)paramPropertyChangeEvent.getOldValue();
        List localList2 = (List)paramPropertyChangeEvent.getNewValue();
        for (localIterator = localList1.iterator(); localIterator.hasNext();) { localTaskService = (TaskService)localIterator.next();
          localTaskService.removePropertyChangeListener(taskServicePCL);
        }
        for (localIterator = localList2.iterator(); localIterator.hasNext();) { localTaskService = (TaskService)localIterator.next();
          localTaskService.addPropertyChangeListener(taskServicePCL);
        }
      }
    }
  }
  
  private class TaskServicePCL
    implements PropertyChangeListener
  {
    private TaskServicePCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ("tasks".equals(str)) {
        List localList1 = (List)paramPropertyChangeEvent.getOldValue();
        List localList2 = (List)paramPropertyChangeEvent.getNewValue();
        TaskMonitor.this.updateTasks(localList1, localList2);
      }
    }
  }
  

  private class TaskPCL
    implements PropertyChangeListener
  {
    private TaskPCL() {}
    

    private void fireStateChange(Task paramTask, String paramString) { firePropertyChange(new PropertyChangeEvent(paramTask, paramString, Boolean.valueOf(false), Boolean.valueOf(true))); }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
      String str = paramPropertyChangeEvent.getPropertyName();
      Task localTask = (Task)paramPropertyChangeEvent.getSource();
      Object localObject = paramPropertyChangeEvent.getNewValue();
      if ((localTask != null) && (localTask == getForegroundTask())) {
        firePropertyChange(paramPropertyChangeEvent);
        if ("state".equals(str)) {
          SwingWorker.StateValue localStateValue = (SwingWorker.StateValue)paramPropertyChangeEvent.getNewValue();
          switch (TaskMonitor.1.$SwitchMap$org$jdesktop$swingworker$SwingWorker$StateValue[localStateValue.ordinal()]) {
          case 1:  fireStateChange(localTask, "pending"); break;
          case 2:  fireStateChange(localTask, "started"); break;
          case 3: 
            fireStateChange(localTask, "done");
            setForegroundTask(null);
          }
        }
      }
    }
  }
}
