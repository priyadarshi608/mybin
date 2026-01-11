package org.jdesktop.application;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingworker.SwingWorker.StateValue;














































































































public abstract class Task<T, V>
  extends SwingWorker<T, V>
{
  private static final Logger logger = Logger.getLogger(Task.class.getName());
  private final Application application;
  private String resourcePrefix;
  private ResourceMap resourceMap;
  private List<TaskListener<T, V>> taskListeners;
  private InputBlocker inputBlocker;
  private String name = null;
  private String title = null;
  private String description = null;
  private long messageTime = -1L;
  private String message = null;
  private long startTime = -1L;
  private long doneTime = -1L;
  private boolean userCanCancel = true;
  private boolean progressPropertyIsValid = false;
  private TaskService taskService = null;
  










  public static enum BlockingScope
  {
    NONE, 
    



    ACTION, 
    



    COMPONENT, 
    



    WINDOW, 
    



    APPLICATION;
    
    private BlockingScope() {} }
  
  private void initTask(ResourceMap paramResourceMap, String paramString) { resourceMap = paramResourceMap;
    if ((paramString == null) || (paramString.length() == 0)) {
      resourcePrefix = "";
    }
    else if (paramString.endsWith(".")) {
      resourcePrefix = paramString;
    }
    else {
      resourcePrefix = (paramString + ".");
    }
    if (paramResourceMap != null) {
      title = paramResourceMap.getString(resourceName("title"), new Object[0]);
      description = paramResourceMap.getString(resourceName("description"), new Object[0]);
      message = paramResourceMap.getString(resourceName("message"), new Object[0]);
      if (message != null) {
        messageTime = System.currentTimeMillis();
      }
    }
    addPropertyChangeListener(new StatePCL(null));
    taskListeners = new CopyOnWriteArrayList();
  }
  
  private ResourceMap defaultResourceMap(Application paramApplication) {
    return paramApplication.getContext().getResourceMap(getClass(), Task.class);
  }
  


























  @Deprecated
  public Task(Application paramApplication, ResourceMap paramResourceMap, String paramString)
  {
    application = paramApplication;
    initTask(paramResourceMap, paramString);
  }
  























  @Deprecated
  public Task(Application paramApplication, String paramString)
  {
    application = paramApplication;
    initTask(defaultResourceMap(paramApplication), paramString);
  }
  





  public Task(Application paramApplication)
  {
    application = paramApplication;
    initTask(defaultResourceMap(paramApplication), "");
  }
  
  public final Application getApplication()
  {
    return application;
  }
  
  public final ApplicationContext getContext() {
    return getApplication().getContext();
  }
  











  public synchronized TaskService getTaskService()
  {
    return taskService;
  }
  

  synchronized void setTaskService(TaskService paramTaskService)
  {
    TaskService localTaskService1;
    
    TaskService localTaskService2;
    synchronized (this) {
      localTaskService1 = taskService;
      taskService = paramTaskService;
      localTaskService2 = taskService;
    }
    firePropertyChange("taskService", localTaskService1, localTaskService2);
  }
  













  protected final String resourceName(String paramString)
  {
    return resourcePrefix + paramString;
  }
  








  public final ResourceMap getResourceMap()
  {
    return resourceMap;
  }
  













  public synchronized String getTitle()
  {
    return title;
  }
  






  protected void setTitle(String paramString)
  {
    String str1;
    




    String str2;
    




    synchronized (this) {
      str1 = title;
      title = paramString;
      str2 = title;
    }
    firePropertyChange("title", str1, str2);
  }
  













  public synchronized String getDescription()
  {
    return description;
  }
  




  protected void setDescription(String paramString)
  {
    String str1;
    



    String str2;
    



    synchronized (this) {
      str1 = description;
      description = paramString;
      str2 = description;
    }
    firePropertyChange("description", str1, str2);
  }
  





  public long getExecutionDuration(TimeUnit paramTimeUnit)
  {
    long l1;
    



    long l2;
    



    synchronized (this) {
      l1 = startTime;
      l2 = doneTime; }
    long l3;
    if (l1 == -1L) {
      l3 = 0L;
    }
    else if (l2 == -1L) {
      l3 = System.currentTimeMillis() - l1;
    }
    else {
      l3 = l2 - l1;
    }
    return paramTimeUnit.convert(Math.max(0L, l3), TimeUnit.MILLISECONDS);
  }
  











  public String getMessage()
  {
    return message;
  }
  












  protected void setMessage(String paramString)
  {
    String str1;
    











    String str2;
    











    synchronized (this) {
      str1 = message;
      message = paramString;
      str2 = message;
      messageTime = System.currentTimeMillis();
    }
    firePropertyChange("message", str1, str2);
  }
  




















  protected final void message(String paramString, Object... paramVarArgs)
  {
    ResourceMap localResourceMap = getResourceMap();
    if (localResourceMap != null) {
      setMessage(localResourceMap.getString(resourceName(paramString), paramVarArgs));
    }
    else {
      setMessage(paramString);
    }
  }
  




  public long getMessageDuration(TimeUnit paramTimeUnit)
  {
    long l1;
    


    synchronized (this) {
      l1 = messageTime;
    }
    long l2 = l1 == -1L ? 0L : Math.max(0L, System.currentTimeMillis() - l1);
    return paramTimeUnit.convert(l2, TimeUnit.MILLISECONDS);
  }
  










  public synchronized boolean getUserCanCancel()
  {
    return userCanCancel;
  }
  





  protected void setUserCanCancel(boolean paramBoolean)
  {
    boolean bool1;
    




    boolean bool2;
    




    synchronized (this) {
      bool1 = userCanCancel;
      userCanCancel = paramBoolean;
      bool2 = userCanCancel;
    }
    firePropertyChange("userCanCancel", Boolean.valueOf(bool1), Boolean.valueOf(bool2));
  }
  















  public synchronized boolean isProgressPropertyValid()
  {
    return progressPropertyIsValid;
  }
  











  protected final void setProgress(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 >= paramInt3) {
      throw new IllegalArgumentException("invalid range: min >= max");
    }
    if ((paramInt1 < paramInt2) || (paramInt1 > paramInt3)) {
      throw new IllegalArgumentException("invalid value");
    }
    float f = (paramInt1 - paramInt2) / (paramInt3 - paramInt2);
    setProgress(Math.round(f * 100.0F));
  }
  






  protected final void setProgress(float paramFloat)
  {
    if ((paramFloat < 0.0D) || (paramFloat > 1.0D)) {
      throw new IllegalArgumentException("invalid percentage");
    }
    setProgress(Math.round(paramFloat * 100.0F));
  }
  











  protected final void setProgress(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat2 >= paramFloat3) {
      throw new IllegalArgumentException("invalid range: min >= max");
    }
    if ((paramFloat1 < paramFloat2) || (paramFloat1 > paramFloat3)) {
      throw new IllegalArgumentException("invalid value");
    }
    float f = (paramFloat1 - paramFloat2) / (paramFloat3 - paramFloat2);
    setProgress(Math.round(f * 100.0F));
  }
  







  public final boolean isPending()
  {
    return getState() == SwingWorker.StateValue.PENDING;
  }
  







  public final boolean isStarted()
  {
    return getState() == SwingWorker.StateValue.STARTED;
  }
  








  protected void process(List<V> paramList)
  {
    fireProcessListeners(paramList);
  }
  
  protected final void done() {
    try {
      if (isCancelled()) {
        cancelled();
      } else {
        try
        {
          succeeded(get());
        }
        catch (InterruptedException localInterruptedException) {
          interrupted(localInterruptedException);
        }
        catch (ExecutionException localExecutionException) {
          failed(localExecutionException.getCause());
        }
      }
    }
    finally {
      try {
        finished();
      }
      finally {
        setTaskService(null);
      }
    }
  }
  











  protected void cancelled() {}
  











  protected void succeeded(T paramT) {}
  











  protected void interrupted(InterruptedException paramInterruptedException) {}
  











  protected void failed(Throwable paramThrowable)
  {
    String str = String.format("%s failed: %s", new Object[] { this, paramThrowable });
    logger.log(Level.SEVERE, str, paramThrowable);
  }
  












  protected void finished() {}
  











  public void addTaskListener(TaskListener<T, V> paramTaskListener)
  {
    if (paramTaskListener == null) {
      throw new IllegalArgumentException("null listener");
    }
    taskListeners.add(paramTaskListener);
  }
  






  public void removeTaskListener(TaskListener<T, V> paramTaskListener)
  {
    if (paramTaskListener == null) {
      throw new IllegalArgumentException("null listener");
    }
    taskListeners.remove(paramTaskListener);
  }
  






  public TaskListener<T, V>[] getTaskListeners()
  {
    return (TaskListener[])taskListeners.toArray(new TaskListener[taskListeners.size()]);
  }
  


  private void fireProcessListeners(List<V> paramList)
  {
    TaskEvent localTaskEvent = new TaskEvent(this, paramList);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.process(localTaskEvent);
    }
  }
  


  private void fireDoInBackgroundListeners()
  {
    TaskEvent localTaskEvent = new TaskEvent(this, null);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.doInBackground(localTaskEvent);
    }
  }
  


  private void fireSucceededListeners(T paramT)
  {
    TaskEvent localTaskEvent = new TaskEvent(this, paramT);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.succeeded(localTaskEvent);
    }
  }
  


  private void fireCancelledListeners()
  {
    TaskEvent localTaskEvent = new TaskEvent(this, null);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.cancelled(localTaskEvent);
    }
  }
  


  private void fireInterruptedListeners(InterruptedException paramInterruptedException)
  {
    TaskEvent localTaskEvent = new TaskEvent(this, paramInterruptedException);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.interrupted(localTaskEvent);
    }
  }
  


  private void fireFailedListeners(Throwable paramThrowable)
  {
    TaskEvent localTaskEvent = new TaskEvent(this, paramThrowable);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.failed(localTaskEvent);
    }
  }
  


  private void fireFinishedListeners()
  {
    TaskEvent localTaskEvent = new TaskEvent(this, null);
    for (TaskListener localTaskListener : taskListeners) {
      localTaskListener.finished(localTaskEvent);
    }
  }
  

  private void fireCompletionListeners()
  {
    try
    {
      if (isCancelled()) {
        fireCancelledListeners();
      } else {
        try
        {
          fireSucceededListeners(get());
        }
        catch (InterruptedException localInterruptedException) {
          fireInterruptedListeners(localInterruptedException);
        }
        catch (ExecutionException localExecutionException) {
          fireFailedListeners(localExecutionException.getCause());
        }
      }
    }
    finally {
      fireFinishedListeners();
    }
  }
  
  private class StatePCL implements PropertyChangeListener { private StatePCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { String str = paramPropertyChangeEvent.getPropertyName();
      if ("state".equals(str)) {
        SwingWorker.StateValue localStateValue = (SwingWorker.StateValue)paramPropertyChangeEvent.getNewValue();
        Task localTask = (Task)paramPropertyChangeEvent.getSource();
        switch (Task.1.$SwitchMap$org$jdesktop$swingworker$SwingWorker$StateValue[localStateValue.ordinal()]) {
        case 1:  taskStarted(localTask); break;
        case 2:  taskDone(localTask);
        }
      }
      else if ("progress".equals(str)) {
        synchronized (Task.this) {
          progressPropertyIsValid = true;
        }
      }
    }
    
    private void taskStarted(Task paramTask) { synchronized (Task.this) {
        startTime = System.currentTimeMillis();
      }
      firePropertyChange("started", Boolean.valueOf(false), Boolean.valueOf(true));
      Task.this.fireDoInBackgroundListeners();
    }
    
    private void taskDone(Task paramTask) { synchronized (Task.this) {
        doneTime = System.currentTimeMillis();
      }
      try {
        paramTask.removePropertyChangeListener(this);
        firePropertyChange("done", Boolean.valueOf(false), Boolean.valueOf(true));
        Task.this.fireCompletionListeners();
      }
      finally {
        firePropertyChange("completed", Boolean.valueOf(false), Boolean.valueOf(true));
      }
    }
  }
  






  public final InputBlocker getInputBlocker()
  {
    return inputBlocker;
  }
  














  public final void setInputBlocker(InputBlocker paramInputBlocker)
  {
    if (getTaskService() != null)
      throw new IllegalStateException("task already being executed");
    InputBlocker localInputBlocker1;
    InputBlocker localInputBlocker2;
    synchronized (this) {
      localInputBlocker1 = inputBlocker;
      inputBlocker = paramInputBlocker;
      localInputBlocker2 = inputBlocker;
    }
    firePropertyChange("inputBlocker", localInputBlocker1, localInputBlocker2);
  }
  










  public static abstract class InputBlocker
    extends AbstractBean
  {
    private final Task task;
    









    private final Task.BlockingScope scope;
    








    private final Object target;
    








    private final ApplicationAction action;
    









    public InputBlocker(Task paramTask, Task.BlockingScope paramBlockingScope, Object paramObject, ApplicationAction paramApplicationAction)
    {
      if (paramTask == null) {
        throw new IllegalArgumentException("null task");
      }
      if (paramTask.getTaskService() != null) {
        throw new IllegalStateException("task already being executed");
      }
      switch (Task.1.$SwitchMap$org$jdesktop$application$Task$BlockingScope[paramBlockingScope.ordinal()]) {
      case 1: 
        if (!(paramObject instanceof Action)) {
          throw new IllegalArgumentException("target not an Action");
        }
        break;
      case 2: 
      case 3: 
        if (!(paramObject instanceof Component)) {
          throw new IllegalArgumentException("target not a Component");
        }
        break;
      }
      task = paramTask;
      scope = paramBlockingScope;
      target = paramObject;
      action = paramApplicationAction;
    }
    










    public InputBlocker(Task paramTask, Task.BlockingScope paramBlockingScope, Object paramObject)
    {
      this(paramTask, paramBlockingScope, paramObject, (paramObject instanceof ApplicationAction) ? (ApplicationAction)paramObject : null);
    }
    







    public final Task getTask()
    {
      return task;
    }
    





    public final Task.BlockingScope getScope()
    {
      return scope;
    }
    








    public final Object getTarget()
    {
      return target;
    }
    













    public final ApplicationAction getAction()
    {
      return action;
    }
    
    protected abstract void block();
    
    protected abstract void unblock();
  }
}
