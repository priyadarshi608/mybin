package org.apache.log4j;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;


































































































public class Category
  implements AppenderAttachable
{
  protected String name;
  protected volatile Level level;
  protected volatile Category parent;
  private static final String FQCN = Category.class.getName();
  



  protected ResourceBundle resourceBundle;
  


  protected LoggerRepository repository;
  


  AppenderAttachableImpl aai;
  


  protected boolean additive = true;
  









  protected Category(String name)
  {
    this.name = name;
  }
  








  public synchronized void addAppender(Appender newAppender)
  {
    if (aai == null) {
      aai = new AppenderAttachableImpl();
    }
    aai.addAppender(newAppender);
    repository.fireAddAppenderEvent(this, newAppender);
  }
  













  public void assertLog(boolean assertion, String msg)
  {
    if (!assertion) {
      error(msg);
    }
  }
  










  public void callAppenders(LoggingEvent event)
  {
    int writes = 0;
    
    for (Category c = this; c != null; c = parent)
    {
      synchronized (c) {
        if (aai != null) {
          writes += aai.appendLoopOnAppenders(event);
        }
        if (!additive) {
          break;
        }
      }
    }
    
    if (writes == 0) {
      repository.emitNoAppenderWarning(this);
    }
  }
  





  synchronized void closeNestedAppenders()
  {
    Enumeration enumeration = getAllAppenders();
    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        Appender a = (Appender)enumeration.nextElement();
        if ((a instanceof AppenderAttachable)) {
          a.close();
        }
      }
    }
  }
  


















  public void debug(Object message)
  {
    if (repository.isDisabled(10000))
      return;
    if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.DEBUG, message, null);
    }
  }
  










  public void debug(Object message, Throwable t)
  {
    if (repository.isDisabled(10000))
      return;
    if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.DEBUG, message, t);
    }
  }
  

















  public void error(Object message)
  {
    if (repository.isDisabled(40000))
      return;
    if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, message, null);
    }
  }
  








  public void error(Object message, Throwable t)
  {
    if (repository.isDisabled(40000))
      return;
    if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, message, t);
    }
  }
  







  /**
   * @deprecated
   */
  public static Logger exists(String name)
  {
    return LogManager.exists(name);
  }
  



















  public void fatal(Object message)
  {
    if (repository.isDisabled(50000))
      return;
    if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.FATAL, message, null);
    }
  }
  








  public void fatal(Object message, Throwable t)
  {
    if (repository.isDisabled(50000))
      return;
    if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.FATAL, message, t);
    }
  }
  



  protected void forcedLog(String fqcn, Priority level, Object message, Throwable t)
  {
    callAppenders(new LoggingEvent(fqcn, this, level, message, t));
  }
  




  public boolean getAdditivity()
  {
    return additive;
  }
  







  public synchronized Enumeration getAllAppenders()
  {
    if (aai == null) {
      return NullEnumeration.getInstance();
    }
    return aai.getAllAppenders();
  }
  






  public synchronized Appender getAppender(String name)
  {
    if ((aai == null) || (name == null)) {
      return null;
    }
    return aai.getAppender(name);
  }
  








  public Level getEffectiveLevel()
  {
    for (Category c = this; c != null; c = parent) {
      if (level != null)
        return level;
    }
    return null;
  }
  


  /**
   * @deprecated
   */
  public Priority getChainedPriority()
  {
    for (Category c = this; c != null; c = parent) {
      if (level != null)
        return level;
    }
    return null;
  }
  








  /**
   * @deprecated
   */
  public static Enumeration getCurrentCategories()
  {
    return LogManager.getCurrentLoggers();
  }
  






  /**
   * @deprecated
   */
  public static LoggerRepository getDefaultHierarchy()
  {
    return LogManager.getLoggerRepository();
  }
  




  /**
   * @deprecated
   */
  public LoggerRepository getHierarchy()
  {
    return repository;
  }
  





  public LoggerRepository getLoggerRepository()
  {
    return repository;
  }
  


  /**
   * @deprecated
   */
  public static Category getInstance(String name)
  {
    return LogManager.getLogger(name);
  }
  

  /**
   * @deprecated
   */
  public static Category getInstance(Class clazz)
  {
    return LogManager.getLogger(clazz);
  }
  




  public final String getName()
  {
    return name;
  }
  










  public final Category getParent()
  {
    return parent;
  }
  







  public final Level getLevel()
  {
    return level;
  }
  

  /**
   * @deprecated
   */
  public final Level getPriority()
  {
    return level;
  }
  



  /**
   * @deprecated
   */
  public static final Category getRoot()
  {
    return LogManager.getRootLogger();
  }
  











  public ResourceBundle getResourceBundle()
  {
    for (Category c = this; c != null; c = parent) {
      if (resourceBundle != null) {
        return resourceBundle;
      }
    }
    return null;
  }
  








  protected String getResourceBundleString(String key)
  {
    ResourceBundle rb = getResourceBundle();
    

    if (rb == null)
    {



      return null;
    }
    try
    {
      return rb.getString(key);
    }
    catch (MissingResourceException mre) {
      error("No resource is associated with key \"" + key + "\"."); }
    return null;
  }
  





















  public void info(Object message)
  {
    if (repository.isDisabled(20000))
      return;
    if (Level.INFO.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, message, null);
    }
  }
  








  public void info(Object message, Throwable t)
  {
    if (repository.isDisabled(20000))
      return;
    if (Level.INFO.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, message, t);
    }
  }
  


  public boolean isAttached(Appender appender)
  {
    if ((appender == null) || (aai == null)) {
      return false;
    }
    return aai.isAttached(appender);
  }
  



































  public boolean isDebugEnabled()
  {
    if (repository.isDisabled(10000))
      return false;
    return Level.DEBUG.isGreaterOrEqual(getEffectiveLevel());
  }
  








  public boolean isEnabledFor(Priority level)
  {
    if (repository.isDisabled(level))
      return false;
    return level.isGreaterOrEqual(getEffectiveLevel());
  }
  







  public boolean isInfoEnabled()
  {
    if (repository.isDisabled(20000))
      return false;
    return Level.INFO.isGreaterOrEqual(getEffectiveLevel());
  }
  









  public void l7dlog(Priority priority, String key, Throwable t)
  {
    if (repository.isDisabled(level)) {
      return;
    }
    if (priority.isGreaterOrEqual(getEffectiveLevel())) {
      String msg = getResourceBundleString(key);
      

      if (msg == null) {
        msg = key;
      }
      forcedLog(FQCN, priority, msg, t);
    }
  }
  








  public void l7dlog(Priority priority, String key, Object[] params, Throwable t)
  {
    if (repository.isDisabled(level)) {
      return;
    }
    if (priority.isGreaterOrEqual(getEffectiveLevel())) {
      String pattern = getResourceBundleString(key);
      String msg;
      String msg; if (pattern == null) {
        msg = key;
      } else
        msg = MessageFormat.format(pattern, params);
      forcedLog(FQCN, priority, msg, t);
    }
  }
  



  public void log(Priority priority, Object message, Throwable t)
  {
    if (repository.isDisabled(level)) {
      return;
    }
    if (priority.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, priority, message, t);
    }
  }
  


  public void log(Priority priority, Object message)
  {
    if (repository.isDisabled(level)) {
      return;
    }
    if (priority.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, priority, message, null);
    }
  }
  








  public void log(String callerFQCN, Priority level, Object message, Throwable t)
  {
    if (repository.isDisabled(level)) {
      return;
    }
    if (level.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(callerFQCN, level, message, t);
    }
  }
  






  private void fireRemoveAppenderEvent(Appender appender)
  {
    if (appender != null) {
      if ((repository instanceof Hierarchy)) {
        ((Hierarchy)repository).fireRemoveAppenderEvent(this, appender);
      } else if ((repository instanceof HierarchyEventListener)) {
        ((HierarchyEventListener)repository).removeAppenderEvent(this, appender);
      }
    }
  }
  







  public synchronized void removeAllAppenders()
  {
    if (aai != null) {
      Vector appenders = new Vector();
      for (Enumeration iter = aai.getAllAppenders(); (iter != null) && (iter.hasMoreElements());) {
        appenders.add(iter.nextElement());
      }
      aai.removeAllAppenders();
      for (Enumeration iter = appenders.elements(); iter.hasMoreElements();) {
        fireRemoveAppenderEvent((Appender)iter.nextElement());
      }
      aai = null;
    }
  }
  







  public synchronized void removeAppender(Appender appender)
  {
    if ((appender == null) || (aai == null))
      return;
    boolean wasAttached = aai.isAttached(appender);
    aai.removeAppender(appender);
    if (wasAttached) {
      fireRemoveAppenderEvent(appender);
    }
  }
  






  public synchronized void removeAppender(String name)
  {
    if ((name == null) || (aai == null)) return;
    Appender appender = aai.getAppender(name);
    aai.removeAppender(name);
    if (appender != null) {
      fireRemoveAppenderEvent(appender);
    }
  }
  




  public void setAdditivity(boolean additive)
  {
    this.additive = additive;
  }
  



  final void setHierarchy(LoggerRepository repository)
  {
    this.repository = repository;
  }
  











  public void setLevel(Level level)
  {
    this.level = level;
  }
  





  /**
   * @deprecated
   */
  public void setPriority(Priority priority)
  {
    level = ((Level)priority);
  }
  








  public void setResourceBundle(ResourceBundle bundle)
  {
    resourceBundle = bundle;
  }
  




















  /**
   * @deprecated
   */
  public static void shutdown() {}
  




















  public void warn(Object message)
  {
    if (repository.isDisabled(30000)) {
      return;
    }
    if (Level.WARN.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, message, null);
    }
  }
  








  public void warn(Object message, Throwable t)
  {
    if (repository.isDisabled(30000))
      return;
    if (Level.WARN.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, message, t);
    }
  }
}
