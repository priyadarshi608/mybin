package org.jdesktop.application;

import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.beans.Beans;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;



































































































@ProxyActions({"cut", "copy", "paste", "delete"})
public abstract class Application
  extends AbstractBean
{
  private static final Logger logger = Logger.getLogger(Application.class.getName());
  private static Application application = null;
  


  private final List<ExitListener> exitListeners;
  


  private final ApplicationContext context;
  


  protected Application()
  {
    exitListeners = new CopyOnWriteArrayList();
    context = new ApplicationContext();
  }
  


















  public static synchronized <T extends Application> void launch(Class<T> paramClass, final String[] paramArrayOfString)
  {
    Runnable local1 = new Runnable() {
      public void run() {
        try {
          Application.access$002(Application.create(val$applicationClass));
          Application.application.initialize(paramArrayOfString);
          Application.application.startup();
          Application.application.waitForReady();
        }
        catch (Exception localException) {
          String str = String.format("Application %s failed to launch", new Object[] { val$applicationClass });
          Application.logger.log(Level.SEVERE, str, localException);
          throw new Error(str, localException);
        }
      }
    };
    SwingUtilities.invokeLater(local1);
  }
  








  static <T extends Application> T create(Class<T> paramClass)
    throws Exception
  {
    if (!Beans.isDesignTime())
    {

      try
      {


        System.setProperty("java.net.useSystemProxies", "true");
      }
      catch (SecurityException localSecurityException1) {}
    }
    







    Constructor localConstructor = paramClass.getDeclaredConstructor(new Class[0]);
    if (!localConstructor.isAccessible()) {
      try {
        localConstructor.setAccessible(true);
      }
      catch (SecurityException localSecurityException2) {}
    }
    

    Application localApplication = (Application)localConstructor.newInstance(new Object[0]);
    


    ApplicationContext localApplicationContext = localApplication.getContext();
    localApplicationContext.setApplicationClass(paramClass);
    localApplicationContext.setApplication(localApplication);
    



    ResourceMap localResourceMap = localApplicationContext.getResourceMap();
    
    localResourceMap.putResource("platform", platform());
    
    if (!Beans.isDesignTime())
    {



      String str1 = "Application.lookAndFeel";
      String str2 = localResourceMap.getString(str1, new Object[0]);
      String str3 = str2 == null ? "system" : str2;
      try {
        if (str3.equalsIgnoreCase("system")) {
          String str4 = UIManager.getSystemLookAndFeelClassName();
          UIManager.setLookAndFeel(str4);
        }
        else if (!str3.equalsIgnoreCase("default")) {
          UIManager.setLookAndFeel(str3);
        }
      }
      catch (Exception localException) {
        String str5 = "Couldn't set LookandFeel " + str1 + " = \"" + str2 + "\"";
        logger.log(Level.WARNING, str5, localException);
      }
    }
    
    return localApplication;
  }
  


  private static String platform()
  {
    String str1 = "default";
    try {
      String str2 = System.getProperty("os.name");
      if ((str2 != null) && (str2.toLowerCase().startsWith("mac os x"))) {
        str1 = "osx";
      }
    }
    catch (SecurityException localSecurityException) {}
    
    return str1;
  }
  

  void waitForReady()
  {
    new DoWaitForEmptyEventQ().execute();
  }
  













  protected void initialize(String[] paramArrayOfString) {}
  













  protected abstract void startup();
  












  protected void ready() {}
  












  protected void shutdown() {}
  












  private static class NotifyingEvent
    extends PaintEvent
    implements ActiveEvent
  {
    private boolean dispatched = false;
    private boolean qEmpty = false;
    
    NotifyingEvent(Component paramComponent) { super(801, null); }
    
    synchronized boolean isDispatched() { return dispatched; }
    synchronized boolean isEventQEmpty() { return qEmpty; }
    
    public void dispatch() { EventQueue localEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
      synchronized (this) {
        qEmpty = (localEventQueue.peekEvent() == null);
        dispatched = true;
        notifyAll();
      }
    }
  }
  


  private void waitForEmptyEventQ()
  {
    boolean bool = false;
    JPanel localJPanel = new JPanel();
    EventQueue localEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    while (!bool) {
      NotifyingEvent localNotifyingEvent = new NotifyingEvent(localJPanel);
      localEventQueue.postEvent(localNotifyingEvent);
      synchronized (localNotifyingEvent) {
        while (!localNotifyingEvent.isDispatched()) {
          try {
            localNotifyingEvent.wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        
        bool = localNotifyingEvent.isEventQEmpty();
      }
    }
  }
  

  private class DoWaitForEmptyEventQ
    extends Task<Void, Void>
  {
    DoWaitForEmptyEventQ() { super(); }
    
    protected Void doInBackground() { Application.this.waitForEmptyEventQ();
      return null;
    }
    
    protected void finished() { ready(); }
  }
  








  public final void exit()
  {
    exit(null);
  }
  









  public static abstract interface ExitListener
    extends EventListener
  {
    public abstract boolean canExit(EventObject paramEventObject);
    









    public abstract void willExit(EventObject paramEventObject);
  }
  









  public void exit(EventObject paramEventObject)
  {
    for (Iterator localIterator = exitListeners.iterator(); localIterator.hasNext();) { localExitListener = (ExitListener)localIterator.next();
      if (!localExitListener.canExit(paramEventObject))
        return;
    }
    try {
      ExitListener localExitListener;
      for (localIterator = exitListeners.iterator(); localIterator.hasNext();) { localExitListener = (ExitListener)localIterator.next();
        try {
          localExitListener.willExit(paramEventObject);
        }
        catch (Exception localException2) {
          logger.log(Level.WARNING, "ExitListener.willExit() failed", localException2);
        }
      }
      shutdown();
    }
    catch (Exception localException1) {
      logger.log(Level.WARNING, "unexpected error in Application.shutdown()", localException1);
    }
    finally {
      end();
    }
  }
  





  protected void end()
  {
    Runtime.getRuntime().exit(0);
  }
  
































  public void addExitListener(ExitListener paramExitListener)
  {
    exitListeners.add(paramExitListener);
  }
  






  public void removeExitListener(ExitListener paramExitListener)
  {
    exitListeners.remove(paramExitListener);
  }
  




  public ExitListener[] getExitListeners()
  {
    int i = exitListeners.size();
    return (ExitListener[])exitListeners.toArray(new ExitListener[i]);
  }
  





  @Action
  public void quit(ActionEvent paramActionEvent)
  {
    exit(paramActionEvent);
  }
  




  public final ApplicationContext getContext()
  {
    return context;
  }
  














  public static synchronized <T extends Application> T getInstance(Class<T> paramClass)
  {
    if (application == null)
    {

      try
      {


        application = create(paramClass);
      }
      catch (Exception localException) {
        String str = String.format("Couldn't construct %s", new Object[] { paramClass });
        throw new Error(str, localException);
      }
    }
    return (Application)paramClass.cast(application);
  }
  




















  public static synchronized Application getInstance()
  {
    if (application == null) {
      application = new NoApplication();
    }
    return application;
  }
  
  private static class NoApplication extends Application {
    protected NoApplication() {
      ApplicationContext localApplicationContext = getContext();
      localApplicationContext.setApplicationClass(getClass());
      localApplicationContext.setApplication(this);
      ResourceMap localResourceMap = localApplicationContext.getResourceMap();
      localResourceMap.putResource("platform", Application.access$300());
    }
    

    protected void startup() {}
  }
  
  public void show(View paramView)
  {
    Window localWindow = (Window)paramView.getRootPane().getParent();
    if (localWindow != null) {
      localWindow.pack();
      localWindow.setVisible(true);
    }
  }
  
  public void hide(View paramView) {
    paramView.getRootPane().getParent().setVisible(false);
  }
}
