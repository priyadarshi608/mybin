package org.jdesktop.application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import javax.swing.JComponent;


































public class ApplicationContext
  extends AbstractBean
{
  private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
  private final List<TaskService> taskServices;
  private final List<TaskService> taskServicesReadOnly;
  private ResourceManager resourceManager;
  private ActionManager actionManager;
  private LocalStorage localStorage;
  private SessionStorage sessionStorage;
  private Application application = null;
  private Class applicationClass = null;
  private JComponent focusOwner = null;
  private Clipboard clipboard = null;
  private Throwable uncaughtException = null;
  private TaskMonitor taskMonitor = null;
  
  protected ApplicationContext()
  {
    resourceManager = new ResourceManager(this);
    actionManager = new ActionManager(this);
    localStorage = new LocalStorage(this);
    sessionStorage = new SessionStorage(this);
    taskServices = new CopyOnWriteArrayList();
    taskServices.add(new TaskService("default"));
    taskServicesReadOnly = Collections.unmodifiableList(taskServices);
  }
  









  public final synchronized Class getApplicationClass()
  {
    return applicationClass;
  }
  










  public final synchronized void setApplicationClass(Class paramClass)
  {
    if (application != null) {
      throw new IllegalStateException("application has been launched");
    }
    applicationClass = paramClass;
  }
  






  public final synchronized Application getApplication()
  {
    return application;
  }
  

  synchronized void setApplication(Application paramApplication)
  {
    if (application != null) {
      throw new IllegalStateException("application has already been launched");
    }
    application = paramApplication;
  }
  







  public final ResourceManager getResourceManager()
  {
    return resourceManager;
  }
  











  protected void setResourceManager(ResourceManager paramResourceManager)
  {
    if (paramResourceManager == null) {
      throw new IllegalArgumentException("null resourceManager");
    }
    ResourceManager localResourceManager = resourceManager;
    resourceManager = paramResourceManager;
    firePropertyChange("resourceManager", localResourceManager, resourceManager);
  }
  


















  public final ResourceMap getResourceMap(Class paramClass)
  {
    return getResourceManager().getResourceMap(paramClass, paramClass);
  }
  




















  public final ResourceMap getResourceMap(Class paramClass1, Class paramClass2)
  {
    return getResourceManager().getResourceMap(paramClass1, paramClass2);
  }
  
















  public final ResourceMap getResourceMap()
  {
    return getResourceManager().getResourceMap();
  }
  




  public final ActionManager getActionManager()
  {
    return actionManager;
  }
  











  protected void setActionManager(ActionManager paramActionManager)
  {
    if (paramActionManager == null) {
      throw new IllegalArgumentException("null actionManager");
    }
    ActionManager localActionManager = actionManager;
    actionManager = paramActionManager;
    firePropertyChange("actionManager", localActionManager, actionManager);
  }
  













  public final ApplicationActionMap getActionMap()
  {
    return getActionManager().getActionMap();
  }
  













  public final ApplicationActionMap getActionMap(Class paramClass, Object paramObject)
  {
    return getActionManager().getActionMap(paramClass, paramObject);
  }
  





  public final ApplicationActionMap getActionMap(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null actionsObject");
    }
    return getActionManager().getActionMap(paramObject.getClass(), paramObject);
  }
  




  public final LocalStorage getLocalStorage()
  {
    return localStorage;
  }
  




  protected void setLocalStorage(LocalStorage paramLocalStorage)
  {
    if (paramLocalStorage == null) {
      throw new IllegalArgumentException("null localStorage");
    }
    LocalStorage localLocalStorage = localStorage;
    localStorage = paramLocalStorage;
    firePropertyChange("localStorage", localLocalStorage, localStorage);
  }
  





  public final SessionStorage getSessionStorage()
  {
    return sessionStorage;
  }
  




  protected void setSessionStorage(SessionStorage paramSessionStorage)
  {
    if (paramSessionStorage == null) {
      throw new IllegalArgumentException("null sessionStorage");
    }
    SessionStorage localSessionStorage = sessionStorage;
    sessionStorage = paramSessionStorage;
    firePropertyChange("sessionStorage", localSessionStorage, sessionStorage);
  }
  


  public Clipboard getClipboard()
  {
    if (clipboard == null) {
      try {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      }
      catch (SecurityException localSecurityException) {
        clipboard = new Clipboard("sandbox");
      }
    }
    return clipboard;
  }
  



  public JComponent getFocusOwner() { return focusOwner; }
  
  void setFocusOwner(JComponent paramJComponent) {
    JComponent localJComponent = focusOwner;
    focusOwner = paramJComponent;
    firePropertyChange("focusOwner", localJComponent, focusOwner);
  }
  
  private List<TaskService> copyTaskServices() {
    return new ArrayList(taskServices);
  }
  
  public void addTaskService(TaskService paramTaskService) {
    if (paramTaskService == null) {
      throw new IllegalArgumentException("null taskService");
    }
    List localList1 = null;List localList2 = null;
    int i = 0;
    synchronized (taskServices) {
      if (!taskServices.contains(paramTaskService)) {
        localList1 = copyTaskServices();
        taskServices.add(paramTaskService);
        localList2 = copyTaskServices();
        i = 1;
      }
    }
    if (i != 0) {
      firePropertyChange("taskServices", localList1, localList2);
    }
  }
  
  public void removeTaskService(TaskService paramTaskService) {
    if (paramTaskService == null) {
      throw new IllegalArgumentException("null taskService");
    }
    List localList1 = null;List localList2 = null;
    int i = 0;
    synchronized (taskServices) {
      if (taskServices.contains(paramTaskService)) {
        localList1 = copyTaskServices();
        taskServices.remove(paramTaskService);
        localList2 = copyTaskServices();
        i = 1;
      }
    }
    if (i != 0) {
      firePropertyChange("taskServices", localList1, localList2);
    }
  }
  
  public TaskService getTaskService(String paramString) {
    if (paramString == null) {
      throw new IllegalArgumentException("null name");
    }
    for (TaskService localTaskService : taskServices) {
      if (paramString.equals(localTaskService.getName())) {
        return localTaskService;
      }
    }
    return null;
  }
  













  public final TaskService getTaskService()
  {
    return getTaskService("default");
  }
  






  public List<TaskService> getTaskServices()
  {
    return taskServicesReadOnly;
  }
  






  public final TaskMonitor getTaskMonitor()
  {
    if (taskMonitor == null) {
      taskMonitor = new TaskMonitor(this);
    }
    return taskMonitor;
  }
}
