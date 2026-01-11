package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;





















































public class Hierarchy
  implements LoggerRepository, RendererSupport, ThrowableRendererSupport
{
  private LoggerFactory defaultFactory;
  private Vector listeners;
  Hashtable ht;
  Logger root;
  RendererMap rendererMap;
  int thresholdInt;
  Level threshold;
  boolean emittedNoAppenderWarning = false;
  boolean emittedNoResourceBundleWarning = false;
  
  private ThrowableRenderer throwableRenderer = null;
  






  public Hierarchy(Logger root)
  {
    ht = new Hashtable();
    listeners = new Vector(1);
    this.root = root;
    
    setThreshold(Level.ALL);
    this.root.setHierarchy(this);
    rendererMap = new RendererMap();
    defaultFactory = new DefaultCategoryFactory();
  }
  



  public void addRenderer(Class classToRender, ObjectRenderer or)
  {
    rendererMap.put(classToRender, or);
  }
  
  public void addHierarchyEventListener(HierarchyEventListener listener)
  {
    if (listeners.contains(listener)) {
      LogLog.warn("Ignoring attempt to add an existent listener.");
    } else {
      listeners.addElement(listener);
    }
  }
  










  public void clear()
  {
    ht.clear();
  }
  

  public void emitNoAppenderWarning(Category cat)
  {
    if (!emittedNoAppenderWarning) {
      LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
      
      LogLog.warn("Please initialize the log4j system properly.");
      LogLog.warn("See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.");
      emittedNoAppenderWarning = true;
    }
  }
  







  public Logger exists(String name)
  {
    Object o = ht.get(new CategoryKey(name));
    if ((o instanceof Logger)) {
      return (Logger)o;
    }
    return null;
  }
  




  public void setThreshold(String levelStr)
  {
    Level l = Level.toLevel(levelStr, null);
    if (l != null) {
      setThreshold(l);
    } else {
      LogLog.warn("Could not convert [" + levelStr + "] to Level.");
    }
  }
  







  public void setThreshold(Level l)
  {
    if (l != null) {
      thresholdInt = level;
      threshold = l;
    }
  }
  
  public void fireAddAppenderEvent(Category logger, Appender appender)
  {
    if (listeners != null) {
      int size = listeners.size();
      
      for (int i = 0; i < size; i++) {
        HierarchyEventListener listener = (HierarchyEventListener)listeners.elementAt(i);
        listener.addAppenderEvent(logger, appender);
      }
    }
  }
  
  void fireRemoveAppenderEvent(Category logger, Appender appender) {
    if (listeners != null) {
      int size = listeners.size();
      
      for (int i = 0; i < size; i++) {
        HierarchyEventListener listener = (HierarchyEventListener)listeners.elementAt(i);
        listener.removeAppenderEvent(logger, appender);
      }
    }
  }
  





  public Level getThreshold()
  {
    return threshold;
  }
  






















  public Logger getLogger(String name)
  {
    return getLogger(name, defaultFactory);
  }
  














  public Logger getLogger(String name, LoggerFactory factory)
  {
    CategoryKey key = new CategoryKey(name);
    




    synchronized (ht) {
      Object o = ht.get(key);
      if (o == null) {
        Logger logger = factory.makeNewLoggerInstance(name);
        logger.setHierarchy(this);
        ht.put(key, logger);
        updateParents(logger);
        return logger; }
      if ((o instanceof Logger))
        return (Logger)o;
      if ((o instanceof ProvisionNode))
      {
        Logger logger = factory.makeNewLoggerInstance(name);
        logger.setHierarchy(this);
        ht.put(key, logger);
        updateChildren((ProvisionNode)o, logger);
        updateParents(logger);
        return logger;
      }
      

      return null;
    }
  }
  










  public Enumeration getCurrentLoggers()
  {
    Vector v = new Vector(ht.size());
    
    Enumeration elems = ht.elements();
    while (elems.hasMoreElements()) {
      Object o = elems.nextElement();
      if ((o instanceof Logger)) {
        v.addElement(o);
      }
    }
    return v.elements();
  }
  
  /**
   * @deprecated
   */
  public Enumeration getCurrentCategories()
  {
    return getCurrentLoggers();
  }
  




  public RendererMap getRendererMap()
  {
    return rendererMap;
  }
  






  public Logger getRootLogger()
  {
    return root;
  }
  





  public boolean isDisabled(int level)
  {
    return thresholdInt > level;
  }
  
  /**
   * @deprecated
   */
  public void overrideAsNeeded(String override)
  {
    LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
  }
  















  public void resetConfiguration()
  {
    getRootLogger().setLevel(Level.DEBUG);
    root.setResourceBundle(null);
    setThreshold(Level.ALL);
    


    synchronized (ht) {
      shutdown();
      
      Enumeration cats = getCurrentLoggers();
      while (cats.hasMoreElements()) {
        Logger c = (Logger)cats.nextElement();
        c.setLevel(null);
        c.setAdditivity(true);
        c.setResourceBundle(null);
      }
    }
    rendererMap.clear();
    throwableRenderer = null;
  }
  


  /**
   * @deprecated
   */
  public void setDisableOverride(String override)
  {
    LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
  }
  





  public void setRenderer(Class renderedClass, ObjectRenderer renderer)
  {
    rendererMap.put(renderedClass, renderer);
  }
  


  public void setThrowableRenderer(ThrowableRenderer renderer)
  {
    throwableRenderer = renderer;
  }
  


  public ThrowableRenderer getThrowableRenderer()
  {
    return throwableRenderer;
  }
  

















  public void shutdown()
  {
    Logger root = getRootLogger();
    

    root.closeNestedAppenders();
    
    synchronized (ht) {
      Enumeration cats = getCurrentLoggers();
      while (cats.hasMoreElements()) {
        Logger c = (Logger)cats.nextElement();
        c.closeNestedAppenders();
      }
      

      root.removeAllAppenders();
      cats = getCurrentLoggers();
      while (cats.hasMoreElements()) {
        Logger c = (Logger)cats.nextElement();
        c.removeAllAppenders();
      }
    }
  }
  






















  private final void updateParents(Logger cat)
  {
    String name = name;
    int length = name.length();
    boolean parentFound = false;
    



    for (int i = name.lastIndexOf('.', length - 1); i >= 0; 
        i = name.lastIndexOf('.', i - 1)) {
      String substr = name.substring(0, i);
      

      CategoryKey key = new CategoryKey(substr);
      Object o = ht.get(key);
      
      if (o == null)
      {
        ProvisionNode pn = new ProvisionNode(cat);
        ht.put(key, pn);
      } else { if ((o instanceof Category)) {
          parentFound = true;
          parent = ((Category)o);
          
          break; }
        if ((o instanceof ProvisionNode)) {
          ((ProvisionNode)o).addElement(cat);
        } else {
          Exception e = new IllegalStateException("unexpected object type " + o.getClass() + " in ht.");
          
          e.printStackTrace();
        }
      }
    }
    if (!parentFound) {
      parent = root;
    }
  }
  
















  private final void updateChildren(ProvisionNode pn, Logger logger)
  {
    int last = pn.size();
    
    for (int i = 0; i < last; i++) {
      Logger l = (Logger)pn.elementAt(i);
      



      if (!parent.name.startsWith(name)) {
        parent = parent;
        parent = logger;
      }
    }
  }
}
